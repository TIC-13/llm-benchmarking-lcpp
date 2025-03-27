package ai.luxai.llmbench.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Alert(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Info,
    text: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .weight(1f),
            imageVector = icon,
            contentDescription = null,
        )
        Text(
            modifier = textModifier
                .weight(3f),
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
fun AlertCard(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Info,
    text: String,
) {
    Alert(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .padding(0.dp, 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        textModifier = modifier
            .padding(20.dp),
        icon = icon,
        text = text
    )
}