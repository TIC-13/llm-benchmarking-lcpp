package ai.luxai.llmbench.state

import ai.luxai.llmbench.utils.getFileFromFolder
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

enum class ModelDownloadStatus {
    DOWNLOADING, DOWNLOADED, NO_DOWNLOAD_STARTED, FAILED
}

fun huggingFaceModelFactory(
    context: Context,
    url: String
): ModelState {

    val getHuggingFaceFileName: () -> String = {
        url.split("/").last()
    }

    val getHuggingFaceModelName: () -> String = {
        getHuggingFaceFileName().split(".").dropLast(1).joinToString(".")
    }

    val getHuggingFaceRepoLink: () -> String = {
        url.split("resolve")[0]
    }

    val getHuggingFaceRepoLabel: () -> String = {
        url.split("co/")[1].split("/resolve")[0]
    }

    return ModelState(
        context,
        url,
        fileName = getHuggingFaceFileName(),
        modelName = getHuggingFaceModelName(),
        repoLink = ModelLink(
            label = getHuggingFaceRepoLabel(),
            address = getHuggingFaceRepoLink()
        )
    )
}

data class ModelLink(
    val label: String,
    val address: String,
)

class ModelState(
    private val context: Context,
    private val url: String,
    val fileName: String,
    val modelName: String,
    val repoLink: ModelLink? = null
){

    val status: MutableState<ModelDownloadStatus> = mutableStateOf(ModelDownloadStatus.NO_DOWNLOAD_STARTED)
    val progress: MutableState<Float> = mutableFloatStateOf(0F)

    var file = getFileIfExists()

    init {
        if(file !== null){
            status.value = ModelDownloadStatus.DOWNLOADED
            progress.value = 1F
        }else {
            status.value = ModelDownloadStatus.NO_DOWNLOAD_STARTED
        }
    }

    fun downloadFile(
        onDownloadFail: (() -> Unit)? = null
    ) {
        if (status.value == ModelDownloadStatus.DOWNLOADING) throw Exception("The download is already going on")
        if (status.value == ModelDownloadStatus.DOWNLOADED) throw Exception("The download has already been done")

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder().url(url).build()
        status.value = ModelDownloadStatus.DOWNLOADING

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val tempDir = getTempModelsDir(context)
                val modelsDir = getModelsDir(context)
                if (!tempDir.exists()) tempDir.mkdirs()
                if (!modelsDir.exists()) modelsDir.mkdirs()

                val tempFile = File(tempDir, fileName)
                val response = client.newCall(request).execute()
                val body = response.body ?: throw Exception("Response body is null")

                val inputStream = body.byteStream()
                // Use BufferedOutputStream for efficient file writing
                val outputStream = BufferedOutputStream(FileOutputStream(tempFile), 8192)

                val buffer = ByteArray(8192) // Increase buffer size to 8KB
                var bytesRead: Int
                var totalBytes = 0L
                val fileSize = body.contentLength()
                var lastUpdateTime = System.currentTimeMillis()

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytes += bytesRead

                    // Throttle UI updates to every 100ms or 1% progress
                    val currentTime = System.currentTimeMillis()
                    val progressValue = if (fileSize > 0) totalBytes.toFloat() / fileSize else 0F
                    if (currentTime - lastUpdateTime >= 100 || progressValue >= progress.value + 0.01F) {
                        withContext(Dispatchers.Main) {
                            progress.value = progressValue
                        }
                        lastUpdateTime = currentTime
                    }
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                val permanentFile = File(modelsDir, fileName)
                val success = tempFile.renameTo(permanentFile)

                withContext(Dispatchers.Main) {
                    if (success) {
                        onSuccess()
                    } else {
                        onFail()
                        if(onDownloadFail !== null)
                            onDownloadFail()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFail()
                    if(onDownloadFail !== null)
                        onDownloadFail()
                }
            }
        }
    }

    fun delete(): Boolean {
        val deleteStatus = file?.delete()
        if(deleteStatus == true) {
            progress.value = 0F
            status.value = ModelDownloadStatus.NO_DOWNLOAD_STARTED
            file = null
        }
        return deleteStatus ?: false
    }

    private fun onFail() {
        status.value = ModelDownloadStatus.FAILED
        eraseModelFromTemp()
        progress.value = 0F
        file = null
    }

    private fun onSuccess() {
        status.value = ModelDownloadStatus.DOWNLOADED
        file = getFileIfExists()
        progress.value = 1F
    }

    private fun getFileIfExists(): File? {
        return getFileFromFolder(getModelsDir(context), fileName)
    }

    private fun getTempFile(): File? {
        val tempFile = File(getTempModelsDir(context), fileName)
        if(!tempFile.exists()) return null
        return tempFile
    }

    private fun eraseModelFromTemp(): Boolean {
        return getTempFile()?.delete() ?: false
    }
}

fun getModelsDir(context: Context): File {
    return File(context.filesDir, "models")
}

fun getTempModelsDir(context: Context): File {
    return File(context.filesDir, "tempModels")
}

fun eraseTempModelsDir(context: Context): Boolean {
    return getTempModelsDir(context).delete()
}
