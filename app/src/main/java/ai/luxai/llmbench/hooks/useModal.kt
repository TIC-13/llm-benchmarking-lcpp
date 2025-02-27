package ai.luxai.llmbench.hooks

import ai.luxai.llmbench.state.ModelDownloadStatus
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class ModalButtonProps(
    val label: String,
    val action: () -> Unit
)

data class ModalProps(
    val title: String,
    val text: String,
    val confirmProps: ModalButtonProps? = null,
    val dismissLabel: String = "Cancel"
)

data class ModalActions(
    val show: (props: ModalProps) -> Unit,
    val hide: () -> Unit
)

@Composable
fun useModal(): ModalActions {

    var modalProps by remember { mutableStateOf<ModalProps?>(null) }

    fun show(props: ModalProps) {
        modalProps = props
    }

    fun hide() {
        modalProps = null
    }

    if(modalProps !== null) {

        val (title, text, confirmProps, dismissLabel) = modalProps!!

        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = text) },
            onDismissRequest = ::hide,
            confirmButton = {
                if(confirmProps !== null)
                    TextButton(onClick = { confirmProps.action(); hide() }) {
                        Text(confirmProps.label)
                    }
            },
            dismissButton = {
                TextButton(onClick = ::hide) { Text(dismissLabel) }
            }
        )
    }

    return ModalActions(
        show = ::show,
        hide = ::hide
    )
}