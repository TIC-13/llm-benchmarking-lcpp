package ai.luxai.llmbench.components

import ai.luxai.llmbench.hooks.ModalProps
import ai.luxai.llmbench.hooks.useModal
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ModalActionIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    disabled: Boolean = false,
    modalProps: ModalProps,
) {

    val modal = useModal()

    ActionIconButton(
        imageVector = imageVector,
        contentDescription = contentDescription,
        onClick = if(!disabled) {{  modal.show(modalProps) }} else null
    )

}