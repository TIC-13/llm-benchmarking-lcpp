package ai.luxai.llmbench.views

import ai.luxai.llmbench.hooks.useCounter
import ai.luxai.llmbench.state.Message
import ai.luxai.llmbench.state.Role
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MessagesView(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    isLoading: Boolean = false,
    isThinking: Boolean = false
) {

    val counter = useCounter(2, delayTime = 250)

    Box(modifier = modifier) {
        val listState = rememberLazyListState()

        if(isLoading)
            return Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(35.dp),
                    color = Color.White
                )
            }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(5.dp, alignment = Alignment.Bottom),
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(items = messages) { item ->
                MessageView(message = item)
                Spacer(modifier = Modifier.height(10.dp))
            }
            item {
                if(isThinking)
                    AppMessageContainer {
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            for(i in 0..2)
                                Icon(
                                    modifier = Modifier.size(if(counter == i) 8.dp else 6.dp),
                                    imageVector = Icons.Filled.Circle,
                                    contentDescription = "Thinking",
                                    tint = Color.Gray
                                )
                        }
                    }
                //MessageView(message = Message(text = "Thinking" + ".".repeat(counter), Role.APP))
            }
            item {
                Spacer(modifier = Modifier.height(1.dp))
            }
        }

        LaunchedEffect(messages) {
            val layoutInfo = listState.layoutInfo
            val maxScroll = layoutInfo.totalItemsCount * layoutInfo.viewportEndOffset.toFloat()
            listState.animateScrollBy(maxScroll)
        }
    }
}

@Composable
fun MessageView(
    modifier: Modifier = Modifier,
    message: Message
) {
    SelectionContainer {
        if (message.role === Role.APP) {
            AppMessageContainer {
                Text(
                    text = message.text,
                    textAlign = TextAlign.Left,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    text = message.text,
                    textAlign = TextAlign.Right,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .wrapContentWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(10.dp)
                        .widthIn(max = 250.dp)
                )
            }
        }
    }
}

@Composable
fun AppMessageContainer(
    modifier: Modifier = Modifier,
    children: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(10.dp)
            .widthIn(max = 250.dp)
            .wrapContentWidth()
    ) {
        children()
    }
}
