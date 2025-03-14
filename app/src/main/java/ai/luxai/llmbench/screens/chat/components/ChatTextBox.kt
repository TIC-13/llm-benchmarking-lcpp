package ai.luxai.llmbench.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatTextBox(
    modifier: Modifier = Modifier,
    canSend: Boolean = false,
    canBeStopped: Boolean = false,
    isLoading: Boolean = false,
    onSend: (String) -> Unit,
    onStop: () -> Unit,
) {

    var text by remember { mutableStateOf("") }

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(text = "Input") },
            modifier = Modifier
                .weight(9f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                cursorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        )
        if(isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(25.dp)
                    .weight(1f)
                    .padding(horizontal = 3.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        }else{
            IconButton(
                onClick = {
                    if(canBeStopped){
                        onStop()
                    }else if(text.trim() !== ""){
                        onSend(text)
                        text = ""
                    }
                },
                modifier = Modifier
                    .aspectRatio(1f)
                    .weight(1f),
                enabled = canSend || canBeStopped
            ) {
                Icon(
                    imageVector = if(canBeStopped) Icons.Filled.Stop else Icons.Filled.Send,
                    contentDescription = "send message",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
