package ai.luxai.llmbench.api

import ai.luxai.llmbench.BuildConfig
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    @POST("report")
    suspend fun postReport(@Body encryptedData: Map<String, String>): Response<Any>
}

const val apiAddress = BuildConfig.API_ADRESS

val retrofit: Retrofit? =
    if (apiAddress.startsWith("http"))
        Retrofit.Builder()
            .baseUrl("$apiAddress/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    else null

val apiService: ApiService? = retrofit?.create(ApiService::class.java)

const val secretKeyString = BuildConfig.API_KEY

@Composable
fun <T> usePostRequest(
    endpoint: String,
    onSuccess: (String) -> Unit,
    onError: (Exception) -> Unit
): Pair<Boolean, (T) -> Unit> {
    var isLoading by remember { mutableStateOf(false) }
    var bodyToSend by remember { mutableStateOf<T?>(null) }
    val gson = remember { Gson() }

    LaunchedEffect(bodyToSend) {
        val body = bodyToSend ?: return@LaunchedEffect
        if (apiService == null) {
            onError(Exception("API service not initialized"))
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val jsonBody = gson.toJson(body)
            val encryptedBody = encryptData(jsonBody, secretKeyString)
            val encryptedDataMap = mapOf("encryptedData" to encryptedBody)

            val rawResponse: String = withContext(Dispatchers.IO) {
                val response = when (endpoint) {
                    "llmInference" -> apiService.createPost(encryptedDataMap)
                    "report" -> apiService.postReport(encryptedDataMap)
                    else -> throw IllegalArgumentException("Unsupported endpoint: $endpoint")
                }
                if (response.isSuccessful) {
                    // Convert the raw response body to String
                    response.body()?.let { body ->
                        when (body) {
                            is String -> body
                            else -> gson.toJson(body)
                        }
                    } ?: throw Exception("Response body is null")
                } else {
                    throw Exception("HTTP ${response.code()}: ${response.message()}")
                }
            }
            withContext(Dispatchers.Main) {
                onSuccess(rawResponse)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("POST", e.toString())
                onError(e)
            }
        } finally {
            isLoading = false
            bodyToSend = null
        }
    }

    val startRequest: (T) -> Unit = { body ->
        bodyToSend = body
    }
    return Pair(isLoading, startRequest)
}

fun encryptData(plainText: String, stringKey: String): String {
    val keyBytes = Base64.getDecoder().decode(stringKey)
    val secretKey = SecretKeySpec(keyBytes, "AES")

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

data class ReportBody(
    val reason: String,
    val details: String
)