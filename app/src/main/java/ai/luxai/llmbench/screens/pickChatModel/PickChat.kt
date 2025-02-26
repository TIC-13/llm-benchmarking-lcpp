package ai.luxai.llmbench.screens.pickChatModel

import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.screens.HomeScreenBackground
import ai.luxai.llmbench.screens.pickChatModel.components.ModelDownloadStatus
import ai.luxai.llmbench.screens.pickChatModel.components.PickModelView
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@ExperimentalMaterial3Api
@Composable
fun PickChatScreen(
    navController: NavController
) {
    val localFocusManager = LocalFocusManager.current
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
        HomeScreenBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 10.dp)
            ) {
                LazyColumn() {
                    items(
                        items = mockedModelDownloadData,
                    ) { (name, status, downloadProgress) ->
                        Spacer(modifier = Modifier.height(15.dp))
                        PickModelView(
                            name = name,
                            status = status,
                            downloadProgress = downloadProgress,
                            onChat = {},
                            onPause = {},
                            onDelete = {},
                            onDownload = {},
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

data class MockedModelDownloadData(
    val name: String,
    val status: ModelDownloadStatus,
    val downloadProgress: Float?
)

val mockedModelDownloadData = listOf(
    MockedModelDownloadData(
        "gemma-2-2b-it-q416_1-MLC",
        status = ModelDownloadStatus.FINISHED,
        downloadProgress = 1F
    ),
    MockedModelDownloadData(
        "gemma-2-2b-it-q416_1-MLC",
        status = ModelDownloadStatus.PAUSED,
        downloadProgress = 0.66F
    ),
    MockedModelDownloadData(
        "gemma-2-2b-it-q416_1-MLC",
        status = ModelDownloadStatus.DOWNLOADING,
        downloadProgress = 0.8f
    ),
    MockedModelDownloadData(
        "gemma-2-2b-it-q416_1-MLC",
        status = ModelDownloadStatus.NO_DOWNLOAD_STARTED,
        downloadProgress = 0F
    ),
)


