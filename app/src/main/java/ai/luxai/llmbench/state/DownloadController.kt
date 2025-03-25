package ai.luxai.llmbench.state

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Downloads a list of models sequentially with interruption support
 * @param context The Android context
 * @param models List of ModelDownloadState objects to download
 * @param onProgress Callback for overall progress (0.0 to 1.0)
 * @param onComplete Callback when all downloads are complete
 * @param onError Callback when any download fails, passing the failed model and exception
 * @return DownloadController instance to manage the download process
 */
class DownloadController(
    private val scope: CoroutineScope,
    private var downloadJob: Job?
) {
    private var isInterrupted = false

    fun interrupt() {
        isInterrupted = true
        scope.launch {
            downloadJob?.cancelAndJoin()
            downloadJob = null
        }
    }

    fun isInterrupted() = isInterrupted

    fun isActive() = downloadJob?.isActive == true
}

fun downloadModelsSequentially(
    models: List<ModelDownloadState>,
    onProgress: ((Float) -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    onError: ((ModelDownloadState, Exception?) -> Unit)? = null
): DownloadController {
    if (models.isEmpty()) {
        onComplete?.invoke()
        return DownloadController(CoroutineScope(Dispatchers.Main + SupervisorJob()), null)
    }

    val modelsFiltered = models.filter {
        it.isCheckedForDownload.value && it.status.value !== ModelDownloadStatus.DOWNLOADED
    }

    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    var downloadJob: Job? = null

    downloadJob = scope.launch {
        try {
            val totalModels = modelsFiltered.size
            var completedModels = 0
            val controller = DownloadController(scope, downloadJob)

            for (model in modelsFiltered) {
                if (controller.isInterrupted()) {
                    break
                }

                // Skip already downloaded models
                if (model.status.value == ModelDownloadStatus.DOWNLOADED) {
                    completedModels++
                    onProgress?.invoke(completedModels.toFloat() / totalModels)
                    continue
                }

                // Wait for the current model to download
                suspendCancellableCoroutine { continuation ->
                    model.downloadFile(
                        onDownloadFail = {
                            continuation.resume(false)
                        }
                    )

                    // Monitor progress and status
                    scope.launch {
                        while (model.status.value == ModelDownloadStatus.DOWNLOADING && !controller.isInterrupted()) {
                            // Calculate overall progress
                            val modelProgress = model.progress.value
                            val overallProgress = (completedModels + modelProgress) / totalModels
                            onProgress?.invoke(overallProgress.coerceIn(0f, 1f))
                            kotlinx.coroutines.delay(100)
                        }

                        if (controller.isInterrupted()) {
                            model.cancelDownload()
                            continuation.resume(false)
                        } else if (model.status.value == ModelDownloadStatus.DOWNLOADED) {
                            completedModels++
                            onProgress?.invoke(completedModels.toFloat() / totalModels)
                            continuation.resume(true)
                        } else if (model.status.value == ModelDownloadStatus.FAILED) {
                            continuation.resume(false)
                        }
                    }
                }.let { success ->
                    if (!success) {
                        if (!controller.isInterrupted()) {
                            onError?.invoke(model, null)
                        }
                        return@launch  // Exit on failure or interruption
                    }
                }
            }

            if (!controller.isInterrupted()) {
                onComplete?.invoke()
            }
        } catch (e: Exception) {
            throw e
        } finally {
            downloadJob = null
        }
    }

    return DownloadController(scope, downloadJob)
}

// Example usage:
/*
fun exampleUsage(context: Context) {
    val models = listOf(
        huggingFaceModelFactory(context, "https://huggingface.co/model1.gguf"),
        huggingFaceModelFactory(context, "https://huggingface.co/model2.gguf")
    )

    val controller = downloadModelsSequentially(
        context = context,
        models = models,
        onProgress = { progress ->
            println("Overall progress: ${progress * 100}%")
        },
        onComplete = {
            println("All models downloaded successfully")
        },
        onError = { failedModel, exception ->
            println("Download failed for ${failedModel.modelName}: ${exception?.message}")
        }
    )

    // To interrupt the downloads later:
    // controller.interrupt()
}
*/