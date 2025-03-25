package ai.luxai.llmbench.state

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
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

    // Interrupt the download process
    suspend fun interrupt() {
        isInterrupted = true
        downloadJob?.cancelAndJoin()
        downloadJob = null
    }

    // Interrupt without suspension (for non-coroutine contexts)
    fun interruptAsync() {
        isInterrupted = true
        scope.launch {
            downloadJob?.cancelAndJoin()
            downloadJob = null
        }
    }

    // Check if the download is interrupted
    fun isInterrupted() = isInterrupted

    // Check if the download is active
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

    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    var downloadJob: Job? = null

    downloadJob = scope.launch {
        try {
            val totalModels = models.size
            var completedModels = 0
            val controller = DownloadController(scope, downloadJob)

            for (model in models) {
                if (controller.isInterrupted()) {
                    break
                }

                // Skip already downloaded models
                if (model.status.value == ModelDownloadStatus.DOWNLOADED) {
                    completedModels++
                    withContext(Dispatchers.Main) {
                        onProgress?.invoke(completedModels.toFloat() / totalModels)
                    }
                    continue
                }

                // Wait for the current model to download
                val success = suspendCancellableCoroutine<Boolean> { continuation ->
                    // Store if we've already resumed to prevent multiple resumes
                    var hasResumed = false

                    model.downloadFile(
                        onDownloadFail = {
                            if (!hasResumed && !controller.isInterrupted()) {
                                hasResumed = true
                                continuation.resume(false)
                            }
                        }
                    )

                    // Monitor progress and status
                    scope.launch {
                        while (model.status.value == ModelDownloadStatus.DOWNLOADING && !controller.isInterrupted()) {
                            val modelProgress = model.progress.value
                            val overallProgress = (completedModels + modelProgress) / totalModels
                            withContext(Dispatchers.Main) {
                                onProgress?.invoke(overallProgress.coerceIn(0f, 1f))
                            }
                            kotlinx.coroutines.delay(100)
                        }

                        if (controller.isInterrupted()) {
                            model.cancelDownload()
                            if (!hasResumed) {
                                hasResumed = true
                                continuation.resume(false)
                            }
                        } else if (model.status.value == ModelDownloadStatus.DOWNLOADED && !hasResumed) {
                            hasResumed = true
                            completedModels++
                            withContext(Dispatchers.Main) {
                                onProgress?.invoke(completedModels.toFloat() / totalModels)
                            }
                            continuation.resume(true)
                        } else if (model.status.value == ModelDownloadStatus.FAILED && !hasResumed) {
                            hasResumed = true
                            continuation.resume(false)
                        }
                    }

                    // Ensure cancellation cleanup
                    continuation.invokeOnCancellation {
                        if (!hasResumed) {
                            hasResumed = true
                            model.cancelDownload()
                            continuation.resume(false)
                        }
                    }
                }

                if (!success || controller.isInterrupted()) {
                    if (!controller.isInterrupted()) {
                        onError?.invoke(model, null)
                    }
                    break
                }
            }

            if (!controller.isInterrupted()) {
                withContext(Dispatchers.Main) {
                    onComplete?.invoke()
                }
            }
        } catch (e: Exception) {
            //if (!isActive()) {  // Only report error if not interrupted
            //    onError?.invoke(models.getOrNull(completedModels), e)
            //}
        } finally {
            downloadJob = null
        }
    }

    return DownloadController(scope, downloadJob)
}