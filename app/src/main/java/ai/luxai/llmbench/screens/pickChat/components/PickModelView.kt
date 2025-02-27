package ai.luxai.llmbench.screens.pickChat.components

import ai.luxai.llmbench.state.ModelDownloadStatus
import ai.luxai.llmbench.state.loadModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PickModelView(
    name: String,
    status: ModelDownloadStatus,
    onDownload: () -> Unit,
    onChat: () -> Unit,
    onDelete: () -> Unit,
    downloadProgress: Float?,
) {

    var deleteOptionsOpened by rememberSaveable { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .wrapContentHeight()
            .defaultMinSize(0.dp, 60.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(top = 15.dp, start = 15.dp, end = 15.dp, bottom = if(deleteOptionsOpened) 0.dp else 15.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = name,
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(8f)
            )
            VerticalDivider(
                modifier = Modifier
                    .height(20.dp)
                    .width(1.dp)
            )

            if (status === ModelDownloadStatus.NO_DOWNLOAD_STARTED ||
                status === ModelDownloadStatus.FAILED
                ) {
                IconButton(
                    onClick = { onDownload() },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Download,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "start downloading",
                    )
                }
            } else if (status === ModelDownloadStatus.DOWNLOADED) {
                IconButton(
                    onClick = {
                        onChat()
                    },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Chat,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "start chatting",
                    )
                }
            } else {
                IconButton(
                    enabled = false, onClick = {}, modifier = Modifier
                        .aspectRatio(1f)
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.HourglassTop,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "pending",
                    )
                }
            }
            if (status == ModelDownloadStatus.DOWNLOADED) {
                IconButton(
                    onClick = { deleteOptionsOpened = true },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "start downloading",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { downloadProgress ?: 0F },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            trackColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        if (deleteOptionsOpened) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                TextButton(onClick = { deleteOptionsOpened = false }) {
                    Text(text = "Cancel", color = MaterialTheme.colorScheme.onPrimary)
                }
                TextButton(onClick = {
                    deleteOptionsOpened = false
                    onDelete()
                }) {
                    Text(text = "Delete model", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

