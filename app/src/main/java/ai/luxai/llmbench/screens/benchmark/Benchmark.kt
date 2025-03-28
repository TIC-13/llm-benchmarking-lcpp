package ai.luxai.llmbench.screens.benchmark

import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.screens.benchmark.hooks.useBenchmarking
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.state.ResultViewModel
import ai.luxai.llmbench.stores.saveResult
import ai.luxai.llmbench.views.MessagesView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Composable
fun BenchmarkScreen(
    navController: NavController,
    viewModel: LLMViewModel,
    resultViewModel: ResultViewModel
) {

    val context = LocalContext.current

    val messages by viewModel.messages.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()

    val modelsDownloadState by viewModel.modelsDownloadState.collectAsState()
    val benchmarkModel = modelsDownloadState.find { it.selectedToBenchmarking.value }

    val gpu by resultViewModel.gpuDisplayValue.collectAsState()
    val ram by resultViewModel.ramDisplayValue.collectAsState()

    val results by resultViewModel.results.collectAsState()

    useBenchmarking(model = benchmarkModel, viewModel = viewModel)

    LaunchedEffect(benchmarkModel) {
        if(benchmarkModel == null){
            //saves benchmarking results
            results.map { result -> saveResult(context, result) }
            //navigates to result screen
            navController.navigate("results")
        }

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
                BenchmarkView(
                    gpu = gpu,
                    ram = ram
                )
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

@Composable
fun BenchmarkView(
    gpu: Double?,
    ram: Double?,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    fontWeight: FontWeight = FontWeight.Light,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Row (
        modifier = modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ){
        Text(text = "GPU: ${if(gpu !== null) "${gpu}%" else "-"}", color = textColor, fontWeight = fontWeight, style = style)
        Text(text = "RAM: ${if(ram !== null) "${ram.toInt()}MB" else "-"}", color = textColor, fontWeight = fontWeight, style = style)
    }
}