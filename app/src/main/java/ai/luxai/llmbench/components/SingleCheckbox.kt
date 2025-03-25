package ai.luxai.llmbench.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            //.size(20.dp) // Set your desired size
            .clickable(
                onClick = { if(enabled) onCheckedChange(!checked) },
                indication = null, // Removes ripple effect padding
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
            tint = if(enabled) MaterialTheme.colorScheme.onPrimary else Color.Gray,
            contentDescription = null,
            //modifier = Modifier.size(20.dp) // Match Box size
        )
    }
}