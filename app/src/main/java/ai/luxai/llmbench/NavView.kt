package ai.luxai.llmbench

import ai.luxai.llmbench.screens.about.AboutScreen
import ai.luxai.llmbench.screens.benchmark.BenchmarkScreen
import ai.luxai.llmbench.screens.chat.ChatScreen
import ai.luxai.llmbench.screens.chatResult.ChatResultScreen
import ai.luxai.llmbench.screens.home.HomeScreen
import ai.luxai.llmbench.screens.licenses.LicensesScreen
import ai.luxai.llmbench.screens.modelSelection.ModelSelectionScreen
import ai.luxai.llmbench.screens.pickChat.PickChatScreen
import ai.luxai.llmbench.screens.results.ResultsScreen
import ai.luxai.llmbench.screens.savedResults.SavedResultsScreen
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.state.ResultViewModel
import ai.luxai.llmbench.state.loadModelsDownloadState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@ExperimentalMaterial3Api
@Composable
fun NavView() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel: LLMViewModel = viewModel(
        factory = LLMViewModel.Factory(loadModelsDownloadState(context))
    )
    val resultViewModel = ResultViewModel(viewModel)

    NavHost(navController = navController, startDestination = "home") {
        composable(
            "home",
            enterTransition = { fadeIn(tween(0)) }, // Instant fade
            exitTransition = { fadeOut(tween(0)) }  // Instant fade
        ) { HomeScreen(navController) }

        composable(
            "about",
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) { AboutScreen(navController) }

        composable(
            "licenses",
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) { LicensesScreen(navController) }

        composable(
            "pick-chat",
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) { PickChatScreen(navController, viewModel, resultViewModel) }

        composable(
            "chat",
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) { ChatScreen(navController, viewModel, resultViewModel) }

        composable(
            "pick-benchmarks",
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) { ModelSelectionScreen(navController, viewModel, resultViewModel) }

        composable(
            "benchmark",
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) { BenchmarkScreen(navController, viewModel, resultViewModel) }

        composable(
            "results",
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) { ResultsScreen(navController, resultViewModel) }

        composable(
            "saved-results",
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) { SavedResultsScreen(navController) }

        composable(
            "chat-results",
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) { ChatResultScreen(navController, resultViewModel) }
    }
}