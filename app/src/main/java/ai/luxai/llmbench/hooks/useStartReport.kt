package ai.luxai.llmbench.hooks

import ai.luxai.llmbench.state.LLMViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun useStartReport(
    viewModel: LLMViewModel,
    navController: NavController
): () -> Unit {

    val modal = useModal()

    fun goToReport() {
        viewModel.setConversationAsReported()
        navController.navigate("report")
    }

    fun openModal() {
        modal.show(
            ModalProps(
                title = "Report conversation",
                text = "Do you wish to report this conversation?",
                confirmProps = ModalButtonProps(label = "Report", action = ::goToReport)
            )
        )
    }

    return { openModal() }

}