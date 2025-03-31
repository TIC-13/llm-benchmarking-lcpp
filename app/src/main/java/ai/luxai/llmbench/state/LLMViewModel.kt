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
    //"https://huggingface.co/bartowski/google_gemma-3-1b-it-GGUF/resolve/main/google_gemma-3-1b-it-Q4_K_M.gguf",
    "https://huggingface.co/bartowski/gemma-2-2b-it-GGUF/resolve/main/gemma-2-2b-it-Q4_K_M.gguf",
    "https://huggingface.co/bartowski/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/Qwen2.5-1.5B-Instruct-Q4_K_M.gguf",
    "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf",
    "https://huggingface.co/bartowski/SmolLM2-1.7B-Instruct-GGUF/resolve/main/SmolLM2-1.7B-Instruct-Q4_K_M.gguf"
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
    val role: Role,
    val toks: Float? = null,
    val prefillTime: Long? = null,
)

enum class ModelState {
    NOT_LOADED,
    LOADING,
    ANSWERING,
    READY,
    STOPPING,
}

class LLMViewModel(
    modelsDownloadState: List<ModelDownloadState>,
) : ViewModel() {

    companion object {
        fun Factory(modelsDownloadState: List<ModelDownloadState>) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LLMViewModel(modelsDownloadState) as T
            }
        }
    }

    private val _modelsDownloadState = MutableStateFlow(modelsDownloadState)
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

    suspend fun loadModel(clearMessages: Boolean = true) {

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

    fun setSelectedModelsToBenchmarking() {
        for (model in _modelsDownloadState.value) {
            model.selectedToBenchmarking.value = model.isCheckedForDownload.value
        }
    }

    private fun getToksOfLastAnswer(): Float {
        return smolLM.getResponseGenerationSpeed()
    }

    fun sendUserQuery(userMessage: String, onError: ((errorMessage: String) -> Unit)?=null, onFinish: (() -> Unit)?=null) {

        if(_modelState.value !== ModelState.READY)
            throw Exception("Model is not ready to answer questions. Current status: ${_modelState.value}")

        if(_model.value == null)
            throw Exception("Cannot send message because model has not been loaded")

        _messages.value = _messages.value.plus(Message(userMessage, Role.USER))

        _modelState.value = ModelState.ANSWERING
        _isThinking.value = true
        val startThinkingTime = System.currentTimeMillis()

        responseGenerationJob =
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    smolLM.getResponse(userMessage).collect {
                        val last = _messages.value.last()
                        _isThinking.value = false
                        if(last.role === Role.USER){
                            _messages.value = _messages.value.plus(
                                Message(it, Role.APP, prefillTime = System.currentTimeMillis() - startThinkingTime)
                            )
                        }else{
                            setLastMessage { lastMessage ->
                                Message(
                                    text = lastMessage.text + it,
                                    role = Role.APP,
                                    prefillTime = lastMessage.prefillTime
                                )
                            }
                        }
                    }

                    setLastMessage {
                        Message(it.text, Role.APP, getToksOfLastAnswer(), it.prefillTime)
                    }

                    if (onFinish != null) {
                        onFinish()
                    }
                }catch(e: CancellationException){
                    //if user cancel, do nothing
                }catch(e: Exception) {
                    e.message?.let {
                        if (onError != null) {
                            onError(it)
                        }
                    }
                }finally {
                    onCompletionJobEnded()
                }
            }
    }

    private fun setLastMessage(action: (last: Message) -> Message) {
        val last = _messages.value.last()

        _messages.value = _messages.value
            .dropLast(1)
            .plus(action(last))
    }

    private fun onCompletionJobEnded() {
        _isThinking.value = false
        responseGenerationJob = null
        _modelState.value = ModelState.READY
    }

    fun stopGeneration(onStop: () -> Unit) {

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
                //stopToast.show()
                onStop()
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

    fun stopAndUnload(onStop: () -> Unit) {
        onFinishPausing = { unload() }
        stopGeneration {onStop()}
    }
}
