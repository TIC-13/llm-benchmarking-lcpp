package ai.luxai.llmbench.screens.benchmark.hooks

import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.state.ModelDownloadState
import ai.luxai.llmbench.state.ModelState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun useBenchmarking(
    model: ModelDownloadState?,
    numMessages: Int = 2,
    viewModel: LLMViewModel
) {

    if(model === null)
        return

    val modelState = viewModel.modelState.collectAsState()
    val modelFromViewModel = viewModel.model.collectAsState()
    val messageCount = remember { mutableIntStateOf(0) }

    fun finishBenchmarkingIfDone() {
        if(messageCount.intValue >= numMessages){
            model.selectedToBenchmarking.value = false
        }
    }

    //unload previous model and load new
    LaunchedEffect(model) {
        viewModel.unload()
        viewModel.setModel(model, onFinishLoading = { messageCount.intValue = 0 })
    }

    LaunchedEffect(modelState.value, modelFromViewModel.value) {
        //Send message
        if(modelState.value == ModelState.READY && modelFromViewModel.value !== null){
            if(messageCount.intValue >= numMessages)
                return@LaunchedEffect

            viewModel.sendUserQuery(userMessage = "Describe who you are in only one word", onFinish = {
                messageCount.intValue += 1
                finishBenchmarkingIfDone()
            })
        }
    }

    //Unload models
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAndUnload {}
        }
    }
}