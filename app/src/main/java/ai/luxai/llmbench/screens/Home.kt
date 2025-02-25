package ai.luxai.llmbench.screens

import ai.luxai.llmbench.R
import ai.luxai.llmbench.hooks.useModal
import ai.luxai.llmbench.hooks.useRankingAddress
import ai.luxai.llmbench.hooks.useRunWithDelayAfter
import ai.luxai.llmbench.utils.navigateToUrl
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

    val (startConversation) = useStartConversation {}

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HomeScreenBackground {
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
                        //.fillMaxHeight(0.3f)
                        .padding(30.dp, 0.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    TitleView()
                }

                Spacer(modifier = Modifier.height(30.dp))

                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {

                    LargeRoundedButton(
                        icon = VectorIcon(Icons.Default.BarChart),
                        onClick = { runWithDelayAfter { }},
                        text = "Start benchmarking"
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    LargeRoundedButton(
                        icon = VectorIcon(Icons.AutoMirrored.Filled.Chat),
                        onClick = { runWithDelayAfter { startConversation() } },
                        text = "Chat with LLMs"
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    LargeRoundedButton(
                        icon = VectorIcon(Icons.Default.MoreTime),
                        onClick = { runWithDelayAfter { } },
                        text = "Last results"
                    )
                    if (rankingAddress.isValid) {
                        Spacer(modifier = Modifier.height(15.dp))

                        LargeRoundedButton(
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

                    LargeRoundedButton(
                        icon = VectorIcon(Icons.Default.Info),
                        onClick = { runWithDelayAfter { } },
                        text = "About app"
                    )
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}


data class StartConversationActions(
    val startConversation: () -> Unit
)

@Composable
fun useStartConversation(
    onStart: () -> Unit
): StartConversationActions {

    val (showStartConversationModal) = useModal(
        title = "Warning",
        text = "\nThe custom conversation option has the same restrictions as the default benchmarking\n" +
                "\nThe execution of LLMs on Android devices can be very taxing, and can cause crashes, especially on devices with less than 8GB of RAM.",
        onConfirm = { onStart() },
        confirmLabel = "Continue"
    )

    return StartConversationActions(
        startConversation = showStartConversationModal
    )
}

@Composable
fun HomeScreenBackground(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceAround,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.motherboard_purple),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }
}

@Composable
fun TitleView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.lightning),
                contentDescription = "Icon in the shape of lightning"
            )
        }
        Text(
            text = stringResource(id = R.string.app_description),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Light
        )
    }
}

sealed class HomeButtonIcon
data class VectorIcon(val icon: ImageVector) : HomeButtonIcon()
data class PainterIcon(val icon: Painter) : HomeButtonIcon()

@Composable
fun LargeRoundedButton(
    icon: HomeButtonIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = "Hello"
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(0.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            when (icon) {
                is VectorIcon ->
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .weight(2f),
                        imageVector = icon.icon,
                        contentDescription = null,
                    )

                is PainterIcon ->
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .weight(2f),
                        painter = icon.icon,
                        contentDescription = null,
                    )
            }
            Text(
                modifier = Modifier
                    .weight(3f),
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}
