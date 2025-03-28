package ai.luxai.llmbench.screens.chatResult

import ai.luxai.llmbench.state.ResultViewModel
import ai.luxai.llmbench.views.ResultView
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController

@Composable
fun ChatResultScreen(
    navController: NavController,
    resultViewModel: ResultViewModel,
) {

    val results by resultViewModel.results.collectAsState()

    fun onContinue() {
        navController.popBackStack("home", false)
    }

    BackHandler { onContinue() }

    ResultView(
        title = "Result",
        onBack = null,
        onContinue = { onContinue() },
        continueLabel = "Continue",
        results = results
    )

}