package ai.luxai.llmbench.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build

data class Phone(
    val brand_name: String,
    val manufacturer: String,
    val phone_model: String,
    val total_ram: Int
)

fun getPhoneData(context: Context): Phone {
    return Phone(
        brand_name = Build.BRAND,
        manufacturer = Build.MANUFACTURER,
        phone_model = Build.MODEL,
        total_ram = getTotalRAM(context).toInt()
    )
}

fun getTotalRAM(context: Context): Long {
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    actManager.getMemoryInfo(memInfo)
    return memInfo.totalMem
}