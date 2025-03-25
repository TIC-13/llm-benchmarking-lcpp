package ai.luxai.llmbench.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ActionIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier.height(30.dp),
    enabledColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabledColor: Color = Color.Gray
) {
    IconButton(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            tint = if (onClick != null) enabledColor else disabledColor,
            contentDescription = contentDescription
        )
    }
}