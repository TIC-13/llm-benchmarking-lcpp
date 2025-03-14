package ai.luxai.llmbench.screens.pickChat

import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.hooks.ModalProps
import ai.luxai.llmbench.hooks.useModal
import ai.luxai.llmbench.screens.pickChat.components.Link
import ai.luxai.llmbench.screens.pickChat.components.PickModelView
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.state.ModelState
import ai.luxai.llmbench.utils.navigateToUrl
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun PickChatScreen(
    navController: NavController,
    viewModel: LLMViewModel,
) {

    val context = LocalContext.current
    val localFocusManager = LocalFocusManager.current

    val modelsDownloadState by viewModel.modelsDownloadState.collectAsState()
    val modelState by viewModel.modelState.collectAsState()

    val canChat = modelState === ModelState.NOT_LOADED

    val modal = useModal()

    val downloadFailedModalProps: (modelName: String) -> ModalProps = {
        modelName ->
            ModalProps(
                title = "Download failed",
                text = "Download failed for the model $modelName",
            )
    }

    val toastStartLoading = Toast.makeText(context, "Loading model...", Toast.LENGTH_SHORT)
    val toastFinishLoading = Toast.makeText(context, "Finished loading", Toast.LENGTH_SHORT)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Model list",
                onBack = {
                    viewModel.unload()
                    navController.popBackStack()
                }
            )
        },
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                localFocusManager.clearFocus()
            })
        }
    )
    { paddingValues ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 10.dp)
            ) {
                LazyColumn() {
                    items(
                        items = modelsDownloadState,
                    ) { item ->
                        Spacer(modifier = Modifier.height(15.dp))
                        PickModelView(
                            canChat = canChat,
                            name = item.modelName,
                            status = item.status.value,
                            downloadProgress = item.progress.value,
                            onChat = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    viewModel.setModel(
                                        item,
                                        onStartLoading = {toastStartLoading.show()},
                                        onFinishLoading = {toastFinishLoading.show()}
                                    )
                                }
                                navController.navigate("chat")
                            },
                            onDelete = { item.delete() },
                            onDownload = { item.downloadFile(onDownloadFail = {
                                modal.show(downloadFailedModalProps(item.modelName))
                            })},
                            onCancel = { item.cancelDownload() },
                            link =
                                if(item.repoLink !== null)
                                    Link(
                                        item.repoLink.label,
                                        onPress = { navigateToUrl(context, item.repoLink.address)}
                                    )
                                else null
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}



