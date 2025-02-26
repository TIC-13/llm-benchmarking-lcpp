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

class ModelState(
    private val context: Context,
    private val url: String,
    ){

    val status: MutableState<ModelDownloadStatus> = mutableStateOf(ModelDownloadStatus.NO_DOWNLOAD_STARTED)
    val progress: MutableState<Float> = mutableFloatStateOf(0F)
    val filename = getFileName()
    val modelName = getModelname()

    var file = getFileIfExists()

    init {
        if(file !== null){
            status.value = ModelDownloadStatus.DOWNLOADED
            progress.value = 1F
        }else {
            status.value = ModelDownloadStatus.NO_DOWNLOAD_STARTED
        }
    }

    private fun getFileName(): String{
        return url.split("/").last()
    }

    private fun getModelname(): String{
        return getFileName().split(".").dropLast(1).joinToString(".")
    }

    private fun getFileIfExists(): File? {
        return getFileFromFolder(getModelsDir(context), filename)
    }

    private fun getTempFile(): File? {
        val tempFile = File(getTempModelsDir(context), filename)
        if(!tempFile.exists()) return null
        return tempFile
    }

    fun downloadFile() {
        if (status.value == ModelDownloadStatus.DOWNLOADING) throw Exception("The download is already going on")
        if (status.value == ModelDownloadStatus.DOWNLOADED) throw Exception("The download has already been done")

        // Optimized OkHttpClient with larger timeouts and better connection handling
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

                val tempFile = File(tempDir, filename)
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

                val permanentFile = File(modelsDir, filename)
                val success = tempFile.renameTo(permanentFile)

                withContext(Dispatchers.Main) {
                    if (success) {
                        status.value = ModelDownloadStatus.DOWNLOADED
                        file = getFileIfExists()
                        progress.value = 1F
                    } else {
                        status.value = ModelDownloadStatus.FAILED
                        tempFile.delete()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    status.value = ModelDownloadStatus.FAILED
                }
                getTempFile()?.delete()
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
