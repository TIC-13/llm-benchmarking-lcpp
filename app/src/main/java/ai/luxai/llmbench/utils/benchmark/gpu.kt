package ai.luxai.llmbench.utils.benchmark

import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

fun gpuUsage(): Double? {
    val filePath = "/sys/kernel/gpu/gpu_busy"
    return try {
        val percentString = File(filePath).readText().trim()
        percentString.dropLast(1).trim().toDouble()
    } catch (e: FileNotFoundException) {
        println("Erro na leitura do arquivo em Mali: $filePath")
        gpuUsageAdreno()
    } catch (e: Exception) {
        println("Error reading file: ${e.message}")
        null
    }
}

private fun gpuUsageAdreno(): Double? {
    val filePath = "/sys/class/kgsl/kgsl-3d0/gpubusy"
    try {
        val process = Runtime.getRuntime().exec("cat $filePath")
        val output = BufferedReader(InputStreamReader(process.inputStream)).readText().trim()
        if(output.startsWith("0"))
            return null
        val pair = output
            .replace("\n", "")
            .split(" ")
            .map {x -> x.trim().toFloat()}
        if(pair[0] == 0F || pair[1] == 0F)
            return null
        return (pair[0].toDouble()/pair[1].toDouble())*100
    }catch(e: FileNotFoundException){
        println("Erro na leitura do arquivo em Adreno: $filePath")
        return null
    }catch(e: Exception){
        println("Outro error em leitura adreno: " + e.message)
        return null
    }
}