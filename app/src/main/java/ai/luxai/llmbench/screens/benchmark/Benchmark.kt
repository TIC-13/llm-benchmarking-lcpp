package ai.luxai.llmbench.screens.benchmark

import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.hooks.useStartReport
import ai.luxai.llmbench.screens.benchmark.hooks.useBenchmarking
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.state.ResultViewModel
import ai.luxai.llmbench.state.Role
import ai.luxai.llmbench.stores.clearResults
import ai.luxai.llmbench.stores.saveResult
import ai.luxai.llmbench.views.MessagesView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    val startReport = useStartReport(viewModel, navController)

    val noMessageFromApp = messages.none { it.role === Role.APP }

    useBenchmarking(model = benchmarkModel, viewModel = viewModel)

    LaunchedEffect(benchmarkModel) {
        if(benchmarkModel == null){
            //clears previous results
            //clearResults(context)
            //saves benchmarking results
            results.map { result -> saveResult(context, result) }
            //navigates to result screen
            navController.navigate("results")
        }
    }

    Scaffold(topBar = {
        AppTopBar(
            title = benchmarkModel?.modelName ?: "Benchmark",
            actions = {
                IconButton(
                    onClick = { startReport() },
                    enabled = !noMessageFromApp
                ) {
                    Icon(
                        imageVector = Icons.Filled.OutlinedFlag,
                        contentDescription = "report chat",
                        tint = if(noMessageFromApp) Color.Gray else MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
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
                    isThinking = isThinking,
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
        BenchmarkText(
            ram, textColor, fontWeight, style
        )
    }
}

@Composable
fun BenchmarkText(
    ram: Double?,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    fontWeight: FontWeight = FontWeight.Light,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Text(text = "RAM: ${if(ram !== null) "${ram.toInt()}MB" else "-"}", color = textColor, fontWeight = fontWeight, style = style)
}