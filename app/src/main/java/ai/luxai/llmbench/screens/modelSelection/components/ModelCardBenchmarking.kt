package ai.luxai.llmbench.screens.modelSelection.components

import ai.luxai.llmbench.components.ActionIconButton
import ai.luxai.llmbench.components.CustomCheckbox
import ai.luxai.llmbench.components.Link
import ai.luxai.llmbench.components.ModalActionIconButton
import ai.luxai.llmbench.components.ModelCard
import ai.luxai.llmbench.props.modal.getCancelDownloadModalProps
import ai.luxai.llmbench.props.modal.getDeleteModalProps
import ai.luxai.llmbench.state.ModelDownloadStatus
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.runtime.Composable

data class CheckboxData(
    val isChecked: Boolean,
    val setChecked: (checked: Boolean) -> Unit,
    val enabled: Boolean = true
)

@Composable
fun ModelCardBenchmarking(
    name: String,
    link: Link?,
    downloadProgress: Float,
    status: ModelDownloadStatus,
    onDownload: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    onCancel: () -> Unit,
    checkbox: CheckboxData,
) {
    ModelCard(name, link, downloadProgress) {
        CustomCheckbox(
            checked = checkbox.isChecked,
            onCheckedChange = checkbox.setChecked,
            enabled = checkbox.enabled
        )

        when (status) {
            ModelDownloadStatus.NO_DOWNLOAD_STARTED -> {
                ActionIconButton(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = "start downloading",
                    onClick = onDownload,
                )
            }

            ModelDownloadStatus.DOWNLOADING -> {
                ModalActionIconButton(
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = "cancel download",
                    modalProps = getCancelDownloadModalProps(onCancel),
                )
            }

            ModelDownloadStatus.DOWNLOADED -> {
                ModalActionIconButton(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "delete model",
                    disabled = onDelete === null,
                    modalProps = getDeleteModalProps { if(onDelete !== null) onDelete() },
                )
            }

            else -> {}
        }
    }
}
