package ai.luxai.llmbench.hooks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember

@Composable
fun useRunWithDelayAfter(delay: Long = 500L): (onPress: () -> Unit) -> Unit {

    val timeStamp = remember { mutableLongStateOf(System.currentTimeMillis()) }

    fun runWithDelayAfter(onPress: () -> Unit){
        val currTime = System.currentTimeMillis()
        if(currTime > timeStamp.longValue + delay){
            onPress()
            timeStamp.longValue = currTime
        }
    }

    return ::runWithDelayAfter
}