package ai.luxai.llmbench.screens.modelSelection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.hooks.ModalProps
import ai.luxai.llmbench.hooks.useCounter
import ai.luxai.llmbench.hooks.useModal
import ai.luxai.llmbench.screens.modelSelection.hooks.DownloadForBenchmarkingState
import ai.luxai.llmbench.screens.modelSelection.hooks.useDownloadForBenchmarking
import ai.luxai.llmbench.screens.pickChat.components.CheckboxAction
import ai.luxai.llmbench.screens.pickChat.components.Link
import ai.luxai.llmbench.screens.pickChat.components.PickModelView
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.utils.navigateToUrl
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ModelSelectionScreen(
    navController: NavController,
    viewModel: LLMViewModel,
) {

    val context = LocalContext.current
    val modelsDownloadState by viewModel.modelsDownloadState.collectAsState()

    val modal = useModal()
    val counter = useCounter(limit = 3)

    val (downloadState, cancelDownloads, startDownloads) = useDownloadForBenchmarking(
        modelsDownloadState
    )

    Scaffold(topBar = {
        AppTopBar(
            title =
            if (downloadState == DownloadForBenchmarkingState.PROGRESS)
                "${" ".repeat(counter)}Downloading${".".repeat(counter)}"
            else
                "Pick your models",
            onBack = {
                cancelDownloads()
                navController.popBackStack()
            }
        )
    }) { paddingValues ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(
                        items = modelsDownloadState,
                    ) { item ->
                        Spacer(modifier = Modifier.height(15.dp))
                        PickModelView(
                            canChat = false,
                            name = item.modelName,
                            status = item.status.value,
                            downloadProgress = item.progress.value,
                            onChat = { },
                            onDelete = { item.delete() },
                            onDownload = {
                                item.downloadFile(onDownloadFail = {
                                    modal.show(downloadFailedModalProps(item.modelName))
                                })
                            },
                            onCancel = { cancelDownloads() },
                            link =
                            if (item.repoLink !== null)
                                Link(
                                    item.repoLink.label,
                                    onPress = { navigateToUrl(context, item.repoLink.address) }
                                )
                            else null,
                            checkboxMode = CheckboxAction(
                                isChecked = item.isCheckedForDownload.value,
                                setChecked = { item.isCheckedForDownload.value = it },
                                enabled = downloadState !== DownloadForBenchmarkingState.PROGRESS
                            ),
                            disableButtons = downloadState == DownloadForBenchmarkingState.PROGRESS
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(15.dp))
                        when (downloadState) {
                            DownloadForBenchmarkingState.NO_MODEL_SELECTED ->
                                Button(onClick = {}) {
                                    IconForButton(Icons.Default.Info)
                                    Text("Select at least a model")
                                }

                            DownloadForBenchmarkingState.NOT_STARTED ->
                                Button(onClick = { startDownloads() }) {
                                    IconForButton(Icons.Default.Download)
                                    Text("Start download for benchmarking")
                                }

                            DownloadForBenchmarkingState.PROGRESS ->
                                Button(onClick = { cancelDownloads() }) {
                                    IconForButton(Icons.Default.Cancel)
                                    Text("Cancel download")
                                }

                            DownloadForBenchmarkingState.FINISHED ->
                                Button(onClick = {}) {
                                    IconForButton(Icons.Default.BarChart)
                                    Text("Start benchmarking")
                                }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun IconForButton(imageVector: ImageVector) {
    Icon(
        imageVector = imageVector,
        "benchmark icon",
        modifier = Modifier.padding(end = 10.dp)
    )
}

val downloadFailedModalProps: (modelName: String) -> ModalProps = { modelName ->
    ModalProps(
        title = "Download failed",
        text = "Download failed for the model $modelName",
    )
}
