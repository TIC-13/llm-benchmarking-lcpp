package ai.luxai.llmbench.screens.home

import ai.luxai.llmbench.R
import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.hooks.useRankingAddress
import ai.luxai.llmbench.hooks.useRunWithDelayAfter
import ai.luxai.llmbench.screens.home.components.HomeScreenButton
import ai.luxai.llmbench.screens.home.components.PainterIcon
import ai.luxai.llmbench.screens.home.components.TitleView
import ai.luxai.llmbench.screens.home.components.VectorIcon
import ai.luxai.llmbench.utils.navigateToUrl
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.MoreTime

@Composable
fun HomeScreen(
    navController: NavController,
) {
    val context = LocalContext.current

    val runWithDelayAfter = useRunWithDelayAfter()
    val rankingAddress = useRankingAddress()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 0.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    TitleView()
                }

                Spacer(modifier = Modifier.height(30.dp))

                Spacer(modifier = Modifier.height(30.dp))

                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {

                    HomeScreenButton(
                        icon = VectorIcon(Icons.Default.BarChart),
                        onClick = { runWithDelayAfter { navController.navigate("pick-benchmarks") }},
                        text = "Start benchmarking"
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    HomeScreenButton(
                        icon = VectorIcon(Icons.AutoMirrored.Filled.Chat),
                        onClick = { runWithDelayAfter { navController.navigate("pick-chat") } },
                        text = "Chat with LLMs"
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    HomeScreenButton(
                        icon = VectorIcon(Icons.Default.MoreTime),
                        onClick = { runWithDelayAfter { navController.navigate("saved-results")} },
                        text = "Last results"
                    )
                    if (rankingAddress.isValid) {
                        Spacer(modifier = Modifier.height(15.dp))

                        HomeScreenButton(
                            icon = PainterIcon(painterResource(R.drawable.web)),
                            onClick = {
                                runWithDelayAfter {
                                    navigateToUrl(
                                        context,
                                        rankingAddress.address
                                    )
                                }
                            },
                            text = "Global ranking"
                        )

                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    HomeScreenButton(
                        icon = VectorIcon(Icons.Default.Info),
                        onClick = { runWithDelayAfter { navController.navigate("about") } },
                        text = "About app"
                    )
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

