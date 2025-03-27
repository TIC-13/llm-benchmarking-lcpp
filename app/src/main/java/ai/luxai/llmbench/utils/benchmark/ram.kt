package ai.luxai.llmbench.utils.benchmark

import android.os.Debug

fun ramUsage(): Double {
    val memoryInfo = Debug.MemoryInfo()
    Debug.getMemoryInfo(memoryInfo)
    return memoryInfo.totalPss.toDouble()/1024
}