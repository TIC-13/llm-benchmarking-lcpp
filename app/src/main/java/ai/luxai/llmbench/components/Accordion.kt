package ai.luxai.llmbench.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AccordionItem(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    backgroundColor: Color = Color.Transparent,
    shape: Shape = RoundedCornerShape(10.dp),
    titleContent: @Composable (Modifier) -> Unit,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(isExpanded) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .clickable {
                expanded = !expanded
            }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            titleContent(Modifier.weight(1f))
            AccordionIcon(
                expanded = expanded,
                color = Color.White
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
fun AccordionTitle(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.White,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    fontWeight: FontWeight = FontWeight.SemiBold
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = style,
        fontWeight = fontWeight
    )
}

@Composable
fun AccordionIcon(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    color: Color = Color.White
) {
    Icon(
        modifier = modifier,
        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
        contentDescription = if (expanded) "Collapse" else "Expand",
        tint = color
    )
}

@Composable
fun AccordionText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.White,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = style
    )
}
