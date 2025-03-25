package ai.luxai.llmbench.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Link(
    val label: String,
    val onPress: () -> Unit
)

@Composable
fun ModelCard(
    name: String,
    link: Link?,
    downloadProgress: Float,
    sideItems: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .wrapContentHeight()
            .defaultMinSize(0.dp, 60.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(top = 15.dp, start = 15.dp, end = 15.dp, bottom = 15.dp)
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
            sideItems()
        }
        if(link !== null) {
            PressableLink(
                fontSize = 12.sp,
                text = link.label,
                onPress = { link.onPress() }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { downloadProgress ?: 0F },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            trackColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}