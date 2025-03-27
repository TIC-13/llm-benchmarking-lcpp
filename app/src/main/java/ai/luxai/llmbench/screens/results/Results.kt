package ai.luxai.llmbench.screens.results

import ai.luxai.llmbench.state.ResultViewModel
import ai.luxai.llmbench.views.ResultView
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController

@Composable
fun ResultsScreen(
    navController: NavController,
    resultViewModel: ResultViewModel,
) {

    val results by resultViewModel.results.collectAsState()

    fun exit() {
        navController.popBackStack("home", false)
    }

    BackHandler { exit() }

    DisposableEffect(Unit) {
        onDispose {
            resultViewModel.resetResults()
        }
    }

    ResultView(
        title = "Result",
        onBack = { exit() },
        onContinue = { exit() },
        continueLabel = "Continue",
        results = results
    )

}