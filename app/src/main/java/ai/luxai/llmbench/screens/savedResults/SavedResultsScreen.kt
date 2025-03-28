package ai.luxai.llmbench.screens.savedResults

import ai.luxai.llmbench.stores.getAllResults
import ai.luxai.llmbench.views.ResultView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun SavedResultsScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val results = getAllResults(context).collectAsState(emptyList())

    fun onBack() {
        navController.popBackStack()
    }

    ResultView(
        title = "Result",
        onBack = { onBack() },
        onContinue = { onBack() },
        continueLabel = "Continue",
        results = results.value
    )
}