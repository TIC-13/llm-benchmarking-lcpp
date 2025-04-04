package ai.luxai.llmbench.screens.pickChat.components

import ai.luxai.llmbench.components.ActionIconButton
import ai.luxai.llmbench.components.Link
import ai.luxai.llmbench.components.ModelCard
import ai.luxai.llmbench.state.ModelDownloadStatus
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.runtime.Composable
import ai.luxai.llmbench.components.ModalActionIconButton
import ai.luxai.llmbench.props.modal.getCancelDownloadModalProps
import ai.luxai.llmbench.props.modal.getDeleteModalProps

@Composable
fun ModelCardChat(
    name: String,
    link: Link?,
    downloadProgress: Float,
    status: ModelDownloadStatus,
    onDownload: () -> Unit,
    canChat: Boolean,
    onChat: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    ModelCard(name, link, downloadProgress) {
        when (status) {
            ModelDownloadStatus.NO_DOWNLOAD_STARTED, ModelDownloadStatus.FAILED -> {
                ActionIconButton(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = "start downloading",
                    onClick = onDownload
                )
            }

            ModelDownloadStatus.DOWNLOADED -> {
                ActionIconButton(
                    imageVector = Icons.Outlined.Chat,
                    contentDescription = "start chatting",
                    onClick = if (canChat) onChat else null
                )

                ModalActionIconButton(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "delete model",
                    modalProps = getDeleteModalProps(onDelete)
                )
            }

            ModelDownloadStatus.DOWNLOADING -> {
                ModalActionIconButton(
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = "cancel download",
                    modalProps = getCancelDownloadModalProps(onCancel)
                )
            }
        }
    }
}
