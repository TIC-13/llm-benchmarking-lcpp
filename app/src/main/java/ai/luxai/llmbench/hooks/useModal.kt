package ai.luxai.llmbench.hooks

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class ModalActions(
    val show: () -> Unit,
    val hide: () -> Unit
)

@Composable
fun useModal(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    confirmLabel: String,
    dismissLabel: String = "Cancel"
): ModalActions {

    var visible by remember { mutableStateOf(false) }

    fun show() {
        visible = true
    }

    fun hide() {
        visible = false
    }

    if(visible) {
        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = text) },
            onDismissRequest = ::hide,
            confirmButton = {
                TextButton(onClick = { onConfirm(); hide() }) { Text(confirmLabel) }
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