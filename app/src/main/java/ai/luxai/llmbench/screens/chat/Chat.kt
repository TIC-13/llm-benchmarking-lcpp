package ai.luxai.llmbench.screens.chat

import ai.luxai.llmbench.components.AppBackground
import ai.luxai.llmbench.components.AppTopBar
import ai.luxai.llmbench.screens.chat.components.ChatTextBox
import ai.luxai.llmbench.views.Message
import ai.luxai.llmbench.views.MessagesView
import ai.luxai.llmbench.views.Role
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ChatScreen(
    navController: NavController
) {
    Scaffold(topBar = {
        AppTopBar(
            title = "Chat",
            onBack = { navController.popBackStack() }
        )
    }) { paddingValues ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                MessagesView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.875f)
                        .padding(horizontal = 10.dp),
                    messages = mockedMessages
                )
                ChatTextBox(
                    modifier = Modifier
                        .fillMaxHeight()
                ) {}
            }
        }
    }
}

val mockedMessages = listOf(
    Message(
        text = "Hey, how's it going?",
        role = Role.USER
    ),
    Message(
        text = "Pretty good! How about you?",
        role = Role.APP
    ),
    Message(
        text = "I'm having a great day so far!",
        role = Role.USER
    ),
    Message(
        text = "Glad to hear that! What's making your day great?",
        role = Role.APP
    ),
    Message(
        text = "Just got a promotion at work!",
        role = Role.USER
    ),
    Message(
        text = "Congratulations! That's awesome news!",
        role = Role.APP
    ),
    Message(
        text = "Thanks! Want to help me celebrate?",
        role = Role.USER
    ),
    Message(
        text = "Sure thing! How about a virtual high-five? ✋",
        role = Role.APP
    ),
    Message(
        text = "Haha, virtual high-five accepted! ✋",
        role = Role.USER
    ),
    Message(
        text = "Sweet! So, what’s the first thing you’ll do with that promotion?",
        role = Role.APP
    ),
    Message(
        text = "Probably treat my team to lunch.",
        role = Role.USER
    ),
    Message(
        text = "Nice move! Good leaders share the love. Where are you taking them?",
        role = Role.APP
    ),
    Message(
        text = "There’s this great Italian place nearby—pizza and pasta for everyone!",
        role = Role.USER
    ),
    Message(
        text = "Sounds delicious! I’d totally crash that party if I could eat pizza.",
        role = Role.APP
    ),
    Message(
        text = "I’ll save you a virtual slice! Any toppings you’d pick?",
        role = Role.USER
    ),
    Message(
        text = "Ooh, tough call, but I’d go with pepperoni and extra cheese. Classic!",
        role = Role.APP
    )
)