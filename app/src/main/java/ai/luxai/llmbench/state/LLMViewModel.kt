package ai.luxai.llmbench.state

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.shubham0204.smollm.SmolLM
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//link to gguf download in hugging face
val huggingFaceUrls = listOf(
    "https://huggingface.co/bartowski/gemma-2-2b-it-GGUF/resolve/main/gemma-2-2b-it-IQ3_M.gguf",
    "https://huggingface.co/Qwen/Qwen2.5-3B-Instruct-GGUF/resolve/main/qwen2.5-3b-instruct-q4_0.gguf",
    "https://huggingface.co/QuantFactory/SmolLM-360M-GGUF/resolve/main/SmolLM-360M.Q8_0.gguf",
    "https://huggingface.co/TheBloke/Mistral-7B-Claude-Chat-GGUF/resolve/main/mistral-7b-claude-chat.Q5_K_S.gguf"
)

fun loadModelsDownloadState(context: Context): List<ModelDownloadState> {
    eraseTempModelsDir(context)
    return huggingFaceUrls.map {
        huggingFaceModelFactory(context, it)
    }
}

enum class Role{
    USER, APP
}

data class Message(
    val text: String,
    val role: Role
)

enum class ModelState {
    NOT_LOADED,
    LOADING,
    ANSWERING,
    READY,
    STOPPING,
}

class LLMViewModel(
    context: Context
) : ViewModel() {

    private val _modelsDownloadState = MutableStateFlow(loadModelsDownloadState(context))
    val modelsDownloadState = _modelsDownloadState.asStateFlow()

    private val _messages = MutableStateFlow(emptyList<Message>())
    val messages = _messages.asStateFlow()

    private val smolLM = SmolLM()

    private val _model = MutableStateFlow<ModelDownloadState?>(null)
    val model = _model.asStateFlow()

    private val _modelState = MutableStateFlow(ModelState.NOT_LOADED)
    val modelState = _modelState.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking = _isThinking.asStateFlow()

    private var responseGenerationJob: Job? = null

    private val stopToast = Toast.makeText(context, "Stopping text generation, please wait...", Toast.LENGTH_LONG)

    private val toastError: (message: String) -> Unit = {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
        }
    }

    var onFinishPausing: (() -> Unit)? = null

    suspend fun setModel(
        md: ModelDownloadState,
        onStartLoading: (() -> Unit)? = null,
        onFinishLoading: (() -> Unit)? = null
    ) {
        if(onStartLoading !== null)
            onStartLoading()

        _model.value = md

        loadModel(clearMessages = true)

        if(onFinishLoading !== null)
            onFinishLoading()
    }

    fun downloadModels(

    ) {
        val downloadStates = _modelsDownloadState.value

        downloadStates.map {
            it.downloadFile()
        }

    }

    private suspend fun loadModel(clearMessages: Boolean = true) {

        val pickedModel = _model.value ?: throw Exception("No model has been setted")

        if(pickedModel.status.value !== ModelDownloadStatus.DOWNLOADED)
            throw Exception("Tried to load model ${pickedModel.modelName} that is not marked as downloaded")

        val pickedModelFile = pickedModel.file ?: throw Exception("The file for the model ${pickedModel.modelName} is null")

        _modelState.value = ModelState.LOADING

        if(clearMessages)
            _messages.value = emptyList()
        smolLM.close()

        smolLM.create(
            modelPath = pickedModelFile.absolutePath,
            minP = 0.05F,
            temperature = 1.0F,
            contextSize = 2048,
            storeChats = false
        )

        smolLM.addSystemPrompt("You are a helpful assistant.")

        if(_modelState.value == ModelState.LOADING)
            _modelState.value = ModelState.READY
    }

    fun unload() {
        if(responseGenerationJob !== null)
            throw Exception("Tried to unload when the model was answering a question")
        smolLM.close()
        _modelState.value = ModelState.NOT_LOADED
    }

    fun sendUserQuery(userMessage: String) {

        if(_modelState.value !== ModelState.READY)
            throw Exception("Model is not ready to answer questions. Current status: ${_modelState.value}")

        if(_model.value == null)
            throw Exception("Cannot send message because model has not been loaded")

        _messages.value = _messages.value.plus(Message(userMessage, Role.USER))

        _modelState.value = ModelState.ANSWERING
        _isThinking.value = true

        responseGenerationJob =
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    smolLM.getResponse(userMessage).collect {
                        _isThinking.value = false
                        val last = _messages.value.last()
                        if(last.role === Role.USER){
                            _messages.value = _messages.value.plus(Message(it, Role.APP))
                        }else{
                            _messages.value = _messages.value.dropLast(1).plus(Message(last.text + it, Role.APP))
                        }
                    }
                }catch(e: CancellationException){
                    //if user cancel, do nothing
                }catch(e: Exception) {
                    e.message?.let { toastError(it) }
                }finally {
                    onCompletionJobEnded()
                }
            }
    }

    private fun onCompletionJobEnded() {
        _isThinking.value = false
        _modelState.value = ModelState.READY
        responseGenerationJob = null
    }

    fun stopGeneration() {

        if(_modelState.value == ModelState.STOPPING)
            return;

        _modelState.value = ModelState.STOPPING

        fun afterCancel() {
            onCompletionJobEnded()
            onFinishPausing?.invoke()
            onFinishPausing = null
        }

        responseGenerationJob?.let { job ->
            if (job.isActive) {
                stopToast.show()
                CoroutineScope(Dispatchers.Default).launch {
                    job.cancel() // Request cancellation
                    job.join()
                    afterCancel()
                }
            }else{
                afterCancel()
            }
        }

        if(responseGenerationJob == null)
            afterCancel()
    }

    fun stopAndUnload() {
        onFinishPausing = { unload() }
        stopGeneration()
    }
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