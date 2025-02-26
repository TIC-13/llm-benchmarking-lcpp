package ai.luxai.llmbench.state

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

val modelsUrl = listOf(
    "https://huggingface.co/bartowski/gemma-2-2b-it-GGUF/resolve/main/gemma-2-2b-it-IQ3_M.gguf",
    "https://huggingface.co/Qwen/Qwen2.5-3B-Instruct-GGUF/resolve/main/qwen2.5-3b-instruct-q4_0.gguf"
)

fun loadModels(context: Context): List<ModelState> {
    eraseTempModelsDir(context)
    return modelsUrl.map {
        ModelState(context, it)
    }
}

class LLMViewModel(
    context: Context
) : ViewModel() {

    private val _modelsState = MutableStateFlow(loadModels(context))
    val modelsState: StateFlow<List<ModelState>> = _modelsState.asStateFlow()

}

class LLMViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LLMViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LLMViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}