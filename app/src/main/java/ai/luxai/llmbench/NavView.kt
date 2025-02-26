package ai.luxai.llmbench

import ai.luxai.llmbench.screens.about.AboutScreen
import ai.luxai.llmbench.screens.home.HomeScreen
import ai.luxai.llmbench.screens.licenses.LicensesScreen
import ai.luxai.llmbench.screens.pickChat.PickChatScreen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@ExperimentalMaterial3Api
@Composable
fun NavView() {

    val navController = rememberNavController()

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
                navController
            )
        }

    }
}