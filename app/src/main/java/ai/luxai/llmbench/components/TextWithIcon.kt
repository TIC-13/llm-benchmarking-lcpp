package ai.luxai.llmbench.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class IconPosition {
    LEFT, RIGHT
}

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    imageVector: ImageVector,
    iconColor: Color,
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    fontColor: Color = MaterialTheme.colorScheme.onPrimary,
    iconPosition: IconPosition = IconPosition.LEFT,
    fontStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {

    val isLeftPositioned = iconPosition == IconPosition.LEFT
    val startPadding = if(isLeftPositioned) 8.dp else 0.dp
    val endPadding = if(isLeftPositioned) 0.dp else 8.dp

    @Composable
    fun getIcon() {
        return Icon(
            modifier = iconModifier
                .size(24.dp),
            imageVector = imageVector,
            contentDescription = "Icon",
            tint = iconColor
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(isLeftPositioned)
            getIcon()
        Text(
            modifier = modifier
                .padding(
                    start = startPadding,
                    end = endPadding
                ),
            text = text,
            fontWeight = fontWeight,
            style = fontStyle,
            color = fontColor,
        )
        if(!isLeftPositioned)
            getIcon()
    }
}