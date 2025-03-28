package ai.luxai.llmbench.stores

import ai.luxai.llmbench.state.BenchmarkResult
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("post_result_datastore")
val gson = Gson()

suspend fun saveResult(context: Context, result: BenchmarkResult) {
    val key = getResultKey(result.llm_model.name)
    context.dataStore.edit { preferences ->
        preferences[key] = result.toJson()
    }
}

fun getResult(context: Context, llmModelName: String): Flow<Unit> {
    val key = getResultKey(llmModelName)
    return context.dataStore.data.map { preferences ->
        preferences[key]?.toBenchmarkingResult()
    }
}

// Retrieve all PostResult entries from DataStore
fun getAllResults(context: Context): Flow<List<BenchmarkResult>> {
    return context.dataStore.data.map { preferences ->
        preferences.asMap().values.mapNotNull { value ->
            (value as? String)?.toBenchmarkingResult()
        }
    }
}

fun BenchmarkResult.toJson(): String {
    return gson.toJson(this)
}

fun String.toBenchmarkingResult(): BenchmarkResult {
    val type = object : TypeToken<BenchmarkResult>() {}.type
    return gson.fromJson(this, type)
}

fun getResultKey(llmModelName: String) = stringPreferencesKey("post_result_$llmModelName")