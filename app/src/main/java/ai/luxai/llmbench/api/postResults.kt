package ai.luxai.llmbench.api

import ai.luxai.llmbench.BuildConfig
import ai.luxai.llmbench.state.BenchmarkResult
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

interface ApiService {
    @POST("llmInference")
    suspend fun createPost(@Body encryptedData: Map<String, String>): Response<Any>
}

const val apiAddress = BuildConfig.API_ADRESS

val retrofit: Retrofit? =
    if(apiAddress.startsWith("http"))
        Retrofit.Builder()
            .baseUrl("$apiAddress/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    else null

val apiService: ApiService? = retrofit?.create(ApiService::class.java)

const val secretKeyString = BuildConfig.API_KEY

fun encryptAndPostResult(postData: BenchmarkResult) {

    if(apiService == null){
        Log.e("post", "API address invalid")
        return
    }

    var encryptedDataMap: Map<String, String>? = null

    try {
        val gson = Gson()
        val postDataJson = gson.toJson(postData)

        val encryptedData = encryptData(postDataJson, secretKeyString)

        encryptedDataMap = mapOf("encryptedData" to encryptedData)
    }catch(e: Exception) {
        Log.e("post", "Error encrypting: $e")
    }

    if(encryptedDataMap == null) return

    GlobalScope.launch(Dispatchers.IO) {
        try {
            val response: Response<Any> = apiService.createPost(encryptedDataMap)
            if (response.isSuccessful) {
                Log.d("post", "Sent encrypted result over network")
            } else {
                Log.d("post", "Post failed " + response.code())
            }
        } catch (e: Exception) {
            Log.e("post", e.toString())
        }
    }
}

fun encryptData(plainText: String, stringKey: String): String {
    // Decode the Base64-encoded key string to get the key bytes
    val keyBytes = Base64.getDecoder().decode(stringKey)
    val secretKey = SecretKeySpec(keyBytes, "AES")  // Use the full keyBytes (should be 32 bytes for AES-256)

    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    val iv = ByteArray(16)
    SecureRandom().nextBytes(iv)
    val ivParameterSpec = IvParameterSpec(iv)

    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)

    val encryptedData = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

    val ivBase64 = Base64.getEncoder().encodeToString(iv)
    val encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData)

    return "$ivBase64:$encryptedDataBase64"
}



