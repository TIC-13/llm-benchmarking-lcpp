package ai.luxai.llmbench.screens.results

import ai.luxai.llmbench.api.encryptAndPostResult
import ai.luxai.llmbench.state.ResultViewModel
import ai.luxai.llmbench.utils.getPhoneData
import ai.luxai.llmbench.views.ResultView
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun ResultsScreen(
    navController: NavController,
    resultViewModel: ResultViewModel,
) {

    val context = LocalContext.current

    val results by resultViewModel.results.collectAsState()

    LaunchedEffect(Unit) {
        val phone = getPhoneData(context)
        results.map {
            it.phone = phone
            encryptAndPostResult(it)
        }
    }

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