package ai.luxai.llmbench.screens.benchmark

import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.screens.benchmark.hooks.useBenchmarking
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.views.MessagesView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BenchmarkScreen(
    navController: NavController,
    viewModel: LLMViewModel,
) {

    val messages by viewModel.messages.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()

    val modelsDownloadState by viewModel.modelsDownloadState.collectAsState()
    val benchmarkModel = modelsDownloadState.find { it.selectedToBenchmarking.value }

    useBenchmarking(model = benchmarkModel, viewModel = viewModel)

    LaunchedEffect(benchmarkModel) {
        if(benchmarkModel == null)
            navController.popBackStack()
    }

    Scaffold(topBar = {
        AppTopBar(
            title = benchmarkModel?.modelName ?: "Benchmark",
            onBack = { navController.popBackStack() }
        )
    }) { paddingValues ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                MessagesView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    messages = messages,
                    isThinking = isThinking
                )
            }
        }
    }

}