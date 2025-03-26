package ai.luxai.llmbench.screens.modelSelection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.components.Link
import ai.luxai.llmbench.hooks.ModalProps
import ai.luxai.llmbench.hooks.useCounter
import ai.luxai.llmbench.hooks.useModal
import ai.luxai.llmbench.screens.modelSelection.components.CheckboxData
import ai.luxai.llmbench.screens.modelSelection.components.ModelCardBenchmarking
import ai.luxai.llmbench.screens.modelSelection.hooks.DownloadForBenchmarkingState
import ai.luxai.llmbench.screens.modelSelection.hooks.useDownloadForBenchmarking
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.state.ModelDownloadStatus
import ai.luxai.llmbench.state.ModelState
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
import androidx.compose.material3.ButtonColors
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
    val modelState by viewModel.modelState.collectAsState()

    val canChat = modelState === ModelState.NOT_LOADED

    val anyIsDownloading = modelsDownloadState.any { it.status.value === ModelDownloadStatus.DOWNLOADING }

    val modal = useModal()
    val counter = useCounter(limit = 3)

    fun startBenchmark() {
        viewModel.setSelectedModelsToBenchmarking()
        navController.navigate("benchmark")
    }

    val (downloadState, cancelDownloads, startDownloads) = useDownloadForBenchmarking(
        modelsDownloadState
    ) {

        startBenchmark()
    }

    val isDownloading = downloadState == DownloadForBenchmarkingState.PROGRESS

    val cancelDownloadButton = @Composable {
        BottomButton(
            imageVector = Icons.Default.Cancel,
            label = "Cancel downloads",
            onClick = { cancelDownloads() }
        )
    }

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
                        ModelCardBenchmarking(
                            name = item.modelName,
                            status = item.status.value,
                            downloadProgress = item.progress.value,
                            onDelete = if(!isDownloading) {{ item.delete() }} else null,
                            onDownload = if(!isDownloading) {{
                                item.downloadFile(onDownloadFail = {
                                    modal.show(downloadFailedModalProps(item.modelName))
                                })
                            }} else null,
                            link =
                            if (item.repoLink !== null)
                                Link(
                                    item.repoLink.label,
                                    onPress = { navigateToUrl(context, item.repoLink.address) }
                                )
                            else null,
                            checkbox = CheckboxData(
                                isChecked = item.isCheckedForDownload.value,
                                setChecked = { item.isCheckedForDownload.value = it },
                                enabled = !isDownloading
                            )
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(15.dp))
                        if(anyIsDownloading){
                            cancelDownloadButton()
                        }else{
                            when (downloadState) {
                                DownloadForBenchmarkingState.NO_MODEL_SELECTED ->
                                    BottomButton(
                                        imageVector = Icons.Default.Info,
                                        label = "Select at least one model",
                                        onClick = {}
                                    )

                                DownloadForBenchmarkingState.NOT_STARTED ->
                                    BottomButton(
                                        imageVector = Icons.Default.Download,
                                        label = "Start download for benchmarking",
                                        onClick = { startDownloads() }
                                    )

                                DownloadForBenchmarkingState.PROGRESS ->
                                    cancelDownloadButton()

                                DownloadForBenchmarkingState.FINISHED ->
                                    BottomButton(
                                        imageVector = Icons.Default.BarChart,
                                        label = "Start benchmarking",
                                        onClick = { startBenchmark() },
                                        enabled = canChat
                                    )
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
fun BottomButton(
    imageVector: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = { onClick() },
        enabled = enabled
    ){
        IconForButton(imageVector)
        Text(label)
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
