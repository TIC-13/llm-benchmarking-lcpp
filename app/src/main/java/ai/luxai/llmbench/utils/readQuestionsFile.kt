package ai.luxai.llmbench.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

fun readQuestionsFile(context: Context, fileName: String): List<String> {
    val inputStream = context.assets.open(fileName)
    val reader = BufferedReader(InputStreamReader(inputStream))
    val stringBuilder = StringBuilder()
    var line: String? = reader.readLine()
    while (line != null) {
        stringBuilder.append(line).append('\n')
        line = reader.readLine()
    }
    reader.close()

    val content = stringBuilder.toString()
    val parts = content.split("\n\n")
    val result = mutableListOf<String>()

    for (part in parts) {
        val lines = part.split("\n")
        if (lines.size >= 2) {
            result.add("${lines[0]} ${lines[1]}")
        }
    }

    return result.toList()
}