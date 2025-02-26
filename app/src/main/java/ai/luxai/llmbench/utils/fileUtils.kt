package ai.luxai.llmbench.utils

import android.content.Context
import java.io.File

fun deleteFile(context: Context, fileName: String): Boolean {
    val file = File(context.filesDir, fileName)
    return if (file.exists()) {
        file.delete()
    } else {
        false
    }
}

fun getFileFromFolder(folder: File, fileName: String): File? {
    if (!folder.exists() || !folder.isDirectory) {
        return null
    }

    val file = File(folder, fileName)
    return if (file.exists() && file.isFile) file else null
}