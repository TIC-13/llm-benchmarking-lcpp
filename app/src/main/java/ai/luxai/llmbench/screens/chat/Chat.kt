package ai.luxai.llmbench.screens.chat

import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.hooks.useCounter
import ai.luxai.llmbench.screens.benchmark.BenchmarkView
import ai.luxai.llmbench.screens.chat.components.ChatTextBox
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.state.ModelState
import ai.luxai.llmbench.state.ResultViewModel
import ai.luxai.llmbench.views.MessagesView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: LLMViewModel,
    resultViewModel: ResultViewModel
) {

    val context = LocalContext.current

    val messages by viewModel.messages.collectAsState()
    val modelState by viewModel.modelState.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val model by viewModel.model.collectAsState()

    val gpu by resultViewModel.gpuDisplayValue.collectAsState()
    val ram by resultViewModel.ramDisplayValue.collectAsState()

    val counter = useCounter(limit = 3)

    val isLoading = modelState === ModelState.LOADING
    val canReload = modelState === ModelState.READY

    fun sendStopToast() {
        val toast = Toast.makeText(context, "Stopping text generation, please wait...", Toast.LENGTH_LONG)
        toast.show()
    }

    fun sendReloadToast() {
        val toast = Toast.makeText(context, "Reloading model...", Toast.LENGTH_LONG)
        toast.show()
    }

    fun sendErrorToast(errorMessage: String) {
        val toast = Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT)
        toast.show()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAndUnload {
                sendStopToast()
            }
        }
    }

    Scaffold(topBar = {
        AppTopBar(
            title =
                if(isLoading)
                    "${" ".repeat(counter)}Loading${".".repeat(counter)}"
                else if(model?.modelName !== null)
                    model?.modelName.toString()
                else
                    "Chat",
            onBack = {
                navController.popBackStack()
            },
            actions = {
                IconButton(
                    enabled = canReload,
                    onClick = {
                        sendReloadToast()
                        CoroutineScope(Dispatchers.Default).launch {
                            viewModel.loadModel()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Replay,
                        contentDescription = "reset the chat",
                        tint = if(canReload) MaterialTheme.colorScheme.onPrimary else Color.Gray
                    )
                }
            }
        )
    }) { paddingValues ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                BenchmarkView(
                    gpu = gpu,
                    ram = ram
                )
                MessagesView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.875f)
                        .padding(horizontal = 10.dp),
                    messages = messages,
                    isThinking = isThinking
                )
                ChatTextBox(
                    modifier = Modifier
                        .fillMaxHeight(),
                    canSend = modelState == ModelState.READY,
                    canBeStopped = modelState == ModelState.ANSWERING,
                    isLoading = isLoading,
                    onStop = { viewModel.stopGeneration { sendStopToast() } },
                    onSend = { viewModel.sendUserQuery(it, onError = { msg -> sendErrorToast(msg) })  }
                )
            }
        }
    }
}
