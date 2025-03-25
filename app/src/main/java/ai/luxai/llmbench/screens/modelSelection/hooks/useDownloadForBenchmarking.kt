package ai.luxai.llmbench.screens.modelSelection.hooks

import ai.luxai.llmbench.state.DownloadController
import ai.luxai.llmbench.state.ModelDownloadState
import ai.luxai.llmbench.state.ModelDownloadStatus
import ai.luxai.llmbench.state.downloadModelsSequentially
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class DownloadForBenchmarkingState {
    NO_MODEL_SELECTED, NOT_STARTED, FINISHED, PROGRESS
}

data class DownloadForBenchmarking(
    val downloadState: DownloadForBenchmarkingState,
    val cancelDownloads: () -> Unit,
    val startDownloads: () -> Unit,
)

@Composable
fun useDownloadForBenchmarking(
    modelsDownloadState: List<ModelDownloadState>
): DownloadForBenchmarking {

    var downloadController by remember { mutableStateOf<DownloadController?>(null) }
    var downloadState by remember { mutableStateOf(DownloadForBenchmarkingState.NOT_STARTED) }

    fun refreshDownloadStatus(
        persistIfProgressing: Boolean = true
    ) {

        if (persistIfProgressing && downloadState == DownloadForBenchmarkingState.PROGRESS)
            return

        val selectedModels = modelsDownloadState
            .filter {
                it.isCheckedForDownload.value
            }

        if (selectedModels.isEmpty()) {
            downloadState = DownloadForBenchmarkingState.NO_MODEL_SELECTED
            return
        }

        val shouldStartDownload = modelsDownloadState
            .filter {
                it.isCheckedForDownload.value
            }
            .any {
                it.status.value != ModelDownloadStatus.DOWNLOADED
            }

        downloadState = if (shouldStartDownload)
            DownloadForBenchmarkingState.NOT_STARTED
        else
            DownloadForBenchmarkingState.FINISHED
    }

    fun cancelDownloads() {
        // Launch interruption in a coroutine instead of blocking
        CoroutineScope(Dispatchers.Main).launch {
            downloadController?.interrupt()
            downloadController = null

            // Cancel any ongoing individual downloads
            modelsDownloadState.forEach { model ->
                if (model.status.value == ModelDownloadStatus.DOWNLOADING) {
                    model.cancelDownload()
                }
            }

            // Refresh status after cancellation
            refreshDownloadStatus(persistIfProgressing = false)
        }
    }

    LaunchedEffect(
        modelsDownloadState.map { it.isCheckedForDownload.value },
        modelsDownloadState.map { it.status.value }
    ) {
        refreshDownloadStatus(persistIfProgressing = true)
    }

    DisposableEffect(modelsDownloadState) {
        onDispose {
            cancelDownloads()
        }
    }

    fun startDownloads() {

        downloadState = DownloadForBenchmarkingState.PROGRESS

        downloadController = downloadModelsSequentially(
            models = modelsDownloadState,
            onComplete = {
                println("All models downloaded")
                downloadState = DownloadForBenchmarkingState.FINISHED
            },
            onError = { failedModel, exception ->
                println("Download failed for ${failedModel.modelName}: ${exception?.message}")
                refreshDownloadStatus(persistIfProgressing = false)
            }
        )
    }

    return DownloadForBenchmarking(
        downloadState = downloadState,
        cancelDownloads = ::cancelDownloads,
        startDownloads = ::startDownloads
    )

}