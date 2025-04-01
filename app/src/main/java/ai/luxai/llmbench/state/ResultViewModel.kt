package ai.luxai.llmbench.state

import ai.luxai.llmbench.api.encryptAndPostResult
import ai.luxai.llmbench.utils.Measurement
import ai.luxai.llmbench.utils.Phone
import ai.luxai.llmbench.utils.Sampler
import ai.luxai.llmbench.utils.benchmark.gpuUsage
import ai.luxai.llmbench.utils.benchmark.ramUsage
import ai.luxai.llmbench.utils.getPhoneData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LLMModel(
    val name: String
)

data class BenchmarkResult(
    var phone: Phone? = null,
    val llm_model: LLMModel,
    //val load_time: Int?,
    val ram: Measurement,
    //val cpu: Measurement,
    val gpu: Measurement,
    val prefill: Measurement,
    val decode: Measurement,
)

class ResultViewModel(
    private val llmViewModel: LLMViewModel
) : ViewModel() {

    private val _results = MutableStateFlow(emptyList<BenchmarkResult>())
    val results: StateFlow<List<BenchmarkResult>> = _results.asStateFlow()

    private var gpuSampler = Sampler()
    private var ramSampler = Sampler()
    private var decodeSampler = Sampler()
    private var prefillTimeSampler = Sampler()

    private var modelName: String? = null

    private val _gpuDisplayValue = MutableStateFlow<Double?>(null)
    val gpuDisplayValue: StateFlow<Double?> = _gpuDisplayValue.asStateFlow()

    private val _ramDisplayValue = MutableStateFlow<Double?>(null)
    val ramDisplayValue: StateFlow<Double?> = _ramDisplayValue.asStateFlow()

    init {
        startSampling()
        startDisplayRefresh()
        observeModelState()
    }

    private fun add(newResult: BenchmarkResult) {
        _results.value += newResult
    }

    fun resetResults() {
        _results.value = emptyList()
        restartBenchmarkingData()
    }

    private fun restartBenchmarkingData() {
        ramSampler = Sampler()
        gpuSampler = Sampler()
        decodeSampler = Sampler()
        prefillTimeSampler = Sampler()
        modelName = null
    }

    private fun addToResults(modelState: ModelState, modelNameCopy: String?) {
        if (modelState != ModelState.NOT_LOADED || modelNameCopy == null) return

        //add tok/s and prefill times to sampler
        llmViewModel.messages.value.map {
            if(it.toks !== null)
                decodeSampler.addSample(it.toks.toDouble())

            if(it.prefillTime !== null)
                prefillTimeSampler.addSample(it.prefillTime.toDouble())
        }

        val result = BenchmarkResult(
            llm_model = LLMModel(name = modelNameCopy),
            ram = ramSampler.getMeasurements(),
            gpu = gpuSampler.getMeasurements(),
            decode = decodeSampler.getMeasurements(),
            prefill = prefillTimeSampler.getMeasurements()
        )

        add(result)

        // Reset samplers and model name
        restartBenchmarkingData()
    }

    private fun startSampling() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(25)
                    if (llmViewModel.modelState.value == ModelState.ANSWERING) {
                        val gpu = gpuUsage()
                        if (gpu != null) gpuSampler.addSample(gpu)
                        ramSampler.addSample(ramUsage())

                        val newModelName = llmViewModel.model.value?.modelName
                        if (newModelName != modelName) {
                            modelName = newModelName
                        }
                    }
                }
            }
        }
    }

    private fun startDisplayRefresh() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(500)
                    if (llmViewModel.modelState.value == ModelState.ANSWERING) {
                        _gpuDisplayValue.value = gpuUsage()
                        _ramDisplayValue.value = ramUsage()
                    }
                }
            }
        }
    }

    private fun observeModelState() {
        viewModelScope.launch {
            llmViewModel.modelState.collect { modelState ->
                if (modelState == ModelState.NOT_LOADED && modelName != null) {
                    addToResults(modelState, modelName)
                }
            }
        }
    }

}