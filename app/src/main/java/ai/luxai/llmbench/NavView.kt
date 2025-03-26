package ai.luxai.llmbench

import ai.luxai.llmbench.screens.about.AboutScreen
import ai.luxai.llmbench.screens.benchmark.BenchmarkScreen
import ai.luxai.llmbench.screens.chat.ChatScreen
import ai.luxai.llmbench.screens.home.HomeScreen
import ai.luxai.llmbench.screens.licenses.LicensesScreen
import ai.luxai.llmbench.screens.modelSelection.ModelSelectionScreen
import ai.luxai.llmbench.screens.pickChat.PickChatScreen
import ai.luxai.llmbench.state.LLMViewModel
import ai.luxai.llmbench.state.loadModelsDownloadState
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

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                navController
            )
        }

        composable("about") {
            AboutScreen(
                navController
            )
        }

        composable("licenses") {
            LicensesScreen(
                navController
            )
        }

        composable("pick-chat") {
            PickChatScreen(
                navController,
                viewModel
            )
        }

        composable("chat") {
            ChatScreen(
                navController,
                viewModel
            )
        }

        composable("pickBenchmarks") {
            ModelSelectionScreen(
                navController,
                viewModel
            )
        }

        composable("benchmark") {
            BenchmarkScreen(
                navController,
                viewModel
            )
        }

    }
}