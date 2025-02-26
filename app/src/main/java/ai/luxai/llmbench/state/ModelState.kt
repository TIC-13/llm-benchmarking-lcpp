package ai.luxai.llmbench.state

import ai.luxai.llmbench.utils.getFileFromFolder
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

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
        return getFileName().split(".")[0]
    }

    private fun getFileIfExists(): File? {
        return getFileFromFolder(getModelsDir(context), filename)
    }

    fun downloadFile() {

        if(status.value == ModelDownloadStatus.DOWNLOADING)
            throw Exception("The dowload is already going on")

        if(status.value == ModelDownloadStatus.DOWNLOADED)
            throw Exception("The download has already been done")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        status.value = ModelDownloadStatus.DOWNLOADING

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Create directories if they don't exist
                val tempDir = getTempModelsDir(context)
                val modelsDir = getModelsDir(context)

                if (!tempDir.exists()) tempDir.mkdirs()
                if (!modelsDir.exists()) modelsDir.mkdirs()

                // Temp file path
                val tempFile = File(tempDir, filename)

                // Start downloading
                val response = client.newCall(request).execute()
                val body = response.body
                if (body != null) {
                    val inputStream = body.byteStream()
                    val outputStream = FileOutputStream(tempFile)

                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    var totalBytes = 0L
                    val fileSize = body.contentLength()

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytes += bytesRead
                        val currProgress = totalBytes.toFloat() / fileSize // Progress as 0.0 to 1.0

                        withContext(Dispatchers.Main) {
                            progress.value = currProgress
                        }
                    }

                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()

                    val permanentFile = File(modelsDir, filename)
                    val success = tempFile.renameTo(permanentFile)

                    if (success) {
                        status.value = ModelDownloadStatus.DOWNLOADED
                        file = getFileIfExists()
                    } else {
                        status.value = ModelDownloadStatus.FAILED
                        tempFile.delete()
                    }

                }
            } catch (e: Exception) {
                status.value = ModelDownloadStatus.FAILED
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
