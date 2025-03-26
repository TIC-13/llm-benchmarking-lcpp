package ai.luxai.llmbench.props.modal

import ai.luxai.llmbench.hooks.ModalButtonProps
import ai.luxai.llmbench.hooks.ModalProps

fun getDeleteModalProps(onDelete: () -> Unit): ModalProps {
    return ModalProps(
        title = "Delete model",
        text = "Do you wish to delete this model?",
        confirmProps = ModalButtonProps(
            label = "Continue",
            action = { onDelete() }
        )
    )
}