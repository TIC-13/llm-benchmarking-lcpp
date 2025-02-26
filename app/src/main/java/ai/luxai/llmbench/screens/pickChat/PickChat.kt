package ai.luxai.llmbench.screens.pickChat

import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.screens.pickChat.components.PickModelView
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.state.ModelDownloadStatus
import ai.luxai.llmbench.state.loadModels
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@ExperimentalMaterial3Api
@Composable
fun PickChatScreen(
    navController: NavController,
    viewModel: LLMViewModel,
) {

    val localFocusManager = LocalFocusManager.current
    val modelsState by viewModel.modelsState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Model list",
                onBack = { navController.popBackStack() }
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
                        items = modelsState,
                    ) { item ->
                        Spacer(modifier = Modifier.height(15.dp))
                        PickModelView(
                            name = item.modelName,
                            status = item.status.value,
                            downloadProgress = item.progress.value,
                            onChat = {},
                            onCancel = {},
                            onDelete = { item.delete() },
                            onDownload = { item.downloadFile() },
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



