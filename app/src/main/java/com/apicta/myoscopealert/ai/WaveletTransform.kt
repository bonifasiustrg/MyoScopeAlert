package com.apicta.myoscopealert.ai

import kotlin.math.pow
import kotlin.math.sqrt

import kotlin.math.pow
import kotlin.math.sqrt

class WaveletTransform {
    // Koefisien Daubechies D4 wavelet
    private val c0 = (1 + sqrt(3.0)) / (4 * sqrt(2.0))
    private val c1 = (3 + sqrt(3.0)) / (4 * sqrt(2.0))
    private val c2 = (3 - sqrt(3.0)) / (4 * sqrt(2.0))
    private val c3 = (1 - sqrt(3.0)) / (4 * sqrt(2.0))

    fun transform(data: FloatArray, level: Int): List<FloatArray> {
        var workingData = data.copyOf()
        val coefficients = mutableListOf<FloatArray>()

        for (l in 0 until level) {
            val (approximation, detail) = decompose(workingData)
            coefficients.add(detail)
            workingData = approximation
        }
        coefficients.add(workingData)
        return coefficients.reversed()
    }

    private fun decompose(data: FloatArray): Pair<FloatArray, FloatArray> {
        val n = data.size
        val halfN = n / 2
        val approximation = FloatArray(halfN)
        val detail = FloatArray(halfN)

        for (i in 0 until halfN) {
            val i2 = i * 2
            approximation[i] = (c0 * data[i2] + c1 * data[(i2 + 1) % n] +
                    c2 * data[(i2 + 2) % n] + c3 * data[(i2 + 3) % n]).toFloat()
            detail[i] = (c3 * data[i2] - c2 * data[(i2 + 1) % n] +
                    c1 * data[(i2 + 2) % n] - c0 * data[(i2 + 3) % n]).toFloat()
        }

        return Pair(approximation, detail)
    }

    companion object {
        fun calculateWaveletFeatures(audioData: FloatArray, level: Int = 4): FloatArray {
            val wavelet = WaveletTransform()
            val coeffs = wavelet.transform(audioData, level)

            val features = mutableListOf<Float>()
            coeffs.forEach { coeff ->
                if (coeff.isNotEmpty()) {
                    features.add(coeff.average().toFloat())
                    features.add(standardDeviation(coeff))
                    features.add(coeff.maxOrNull() ?: 0f)
                    features.add(coeff.minOrNull() ?: 0f)
                }
            }

            // Calculate additional statistics for all wavelet features
            val featuresArray = features.toFloatArray()
            return floatArrayOf(
                featuresArray.average().toFloat(),
                standardDeviation(featuresArray),
                featuresArray.maxOrNull() ?: 0f,
                featuresArray.minOrNull() ?: 0f,
                median(featuresArray),
                variance(featuresArray),
                skewness(featuresArray),
                percentile(featuresArray, 25f),
                percentile(featuresArray, 75f),
                percentile(featuresArray, 75f) - percentile(featuresArray, 25f),
                (featuresArray.maxOrNull() ?: 0f) - (featuresArray.minOrNull() ?: 0f),
                kurtosis(featuresArray)
            )
        }

        // Statistical functions
        fun standardDeviation(data: FloatArray): Float {
            val mean = data.average()
            return sqrt(data.map { (it - mean).pow(2) }.average()).toFloat()
        }

        fun variance(data: FloatArray): Float {
            val mean = data.average()
            return data.map { (it - mean).pow(2) }.average().toFloat()
        }

        fun skewness(data: FloatArray): Float {
            val mean = data.average()
            val std = standardDeviation(data)
            return data.map { ((it - mean) / std).pow(3) }.average().toFloat()
        }

        fun kurtosis(data: FloatArray): Float {
            val mean = data.average()
            val std = standardDeviation(data)
            return data.map { ((it - mean) / std).pow(4) }.average().toFloat() - 3f
        }

        fun median(data: FloatArray): Float {
            val sorted = data.sorted()
            val middle = sorted.size / 2
            return if (sorted.size % 2 == 0) {
                (sorted[middle - 1] + sorted[middle]) / 2
            } else {
                sorted[middle]
            }
        }

        fun percentile(data: FloatArray, percentile: Float): Float {
            val sorted = data.sorted()
            val index = (percentile / 100 * (sorted.size - 1)).toInt()
            return sorted[index]
        }
    }
}