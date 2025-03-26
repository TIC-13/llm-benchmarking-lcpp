package ai.luxai.llmbench.props.modal

import ai.luxai.llmbench.hooks.ModalButtonProps
import ai.luxai.llmbench.hooks.ModalProps

fun getCancelDownloadModalProps(onCancel: () -> Unit): ModalProps {
    return ModalProps(
        title = "Cancel download",
        text = "Do you wish to cancel this download?",
        confirmProps = ModalButtonProps(
            label = "Continue",
            action = { onCancel() }
        )
    )
}