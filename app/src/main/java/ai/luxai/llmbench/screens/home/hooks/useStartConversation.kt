package ai.luxai.llmbench.screens.home.hooks

import ai.luxai.llmbench.hooks.useModal
import androidx.compose.runtime.Composable

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