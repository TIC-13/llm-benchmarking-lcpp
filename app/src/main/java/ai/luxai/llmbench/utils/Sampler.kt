package ai.luxai.llmbench.utils

import kotlin.math.sqrt
class Sampler {

    private val samples = arrayListOf<Double>()
    private var peak = Double.MIN_VALUE
    private var creationTime = 0L

    init {
        creationTime = System.currentTimeMillis()
    }

    @Synchronized
    fun addSample(sample: Double): Unit {
        if(sample > peak) peak = sample
        samples.add(sample)
    }

    @Synchronized
    fun getSamples(): List<Double> {
        return ArrayList(samples)
    }

    @Synchronized
    fun sum(): Double? {
        if(samples.isEmpty()) return null
        return samples.sum()
    }

    @Synchronized
    fun average(): Double? {
        if(samples.isEmpty()) return null
        return samples.average()
    }

    @Synchronized
    fun peak(): Double? {
        if(samples.isEmpty()) return null
        return peak
    }

    @Synchronized
    fun std(): Double? {
        if(samples.isEmpty()) return null
        val mean = samples.average()
        val sumOfSquaredDiffs = samples.sumOf { (it - mean) * (it - mean) }
        val variance = sumOfSquaredDiffs / samples.size
        return sqrt(variance)
    }

    @Synchronized
    fun median(): Double? {
        if (samples.isEmpty()) return null

        val sortedValues = samples.sorted()
        val middle = sortedValues.size / 2

        return if (sortedValues.size % 2 == 0) {
            (sortedValues[middle - 1] + sortedValues[middle]) / 2.0
        } else {
            sortedValues[middle]
        }
    }

    fun getCreationTime(): Long {
        return creationTime
    }

    fun getMeasurements(): Measurement {
        return Measurement(
            average = this.average(),
            peak = this.peak(),
            std = this.std(),
            median = this.median(),
            sum = this.sum()
        )
    }
}

data class Measurement(
    val average: Double?,
    val median: Double?,
    val peak: Double?,
    val std: Double?,
    val sum: Double?,
)
