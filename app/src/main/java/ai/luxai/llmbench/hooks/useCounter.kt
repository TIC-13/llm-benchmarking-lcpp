package ai.luxai.llmbench.hooks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun useCounter(limit: Int, delayTime: Long = 1000): Int {

    var counter by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Default) {
            while(true){
                delay(delayTime)
                if(counter >= limit) counter = 0 else counter ++
            }
        }
    }

    return counter
}