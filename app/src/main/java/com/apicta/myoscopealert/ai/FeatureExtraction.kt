package com.apicta.myoscopealert.ai

import org.jtransforms.fft.FloatFFT_1D
import kotlin.math.log2
import kotlin.math.pow
import android.content.Context
import android.net.Uri
import com.apicta.myoscopealert.readWavFile
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import kotlin.math.*

class AudioFeatureExtractor {
    fun extractFeatures(context: Context, wavFilePath: String): FloatArray {
        val audioData = readWavFile(wavFilePath)
//        val audioData = loadAudioData(context, uri)
        val sampleRate = 22050 // Assuming sample rate, adjust if different

        // MFCCs
        val mfccs = calculateMFCCs(audioData, sampleRate)
        val mfccFeatures = calculateMFCCFeatures(mfccs)

        // Wavelet features
        val waveletFeatures = calculateWaveletFeatures(audioData)

        // Combine all features
        return mfccFeatures + waveletFeatures
    }
    private fun log(value: Float): Float {
        return kotlin.math.ln(value.toDouble()).toFloat() // Menggunakan ln() untuk logaritma natural
    }
    private fun loadAudioData(context: Context, uri: Uri): FloatArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: throw IllegalArgumentException("Failed to read audio file")
        inputStream.close()

        // Convert bytes to float array (assuming 16-bit PCM)
        return FloatArray(bytes.size / 2) { i ->
            val sample = (bytes[i * 2 + 1].toInt() shl 8) or (bytes[i * 2].toInt() and 0xFF)
            sample / 32768f
        }
    }

    private fun calculateMFCCs(audioData: FloatArray, sampleRate: Int): Array<FloatArray> {
        val fft = FloatFFT_1D(audioData.size.toLong())
        val fftData = audioData.copyOf()
        fft.realForward(fftData)

        val melFilterbank = createMelFilterbank(40, audioData.size, sampleRate)
        val melSpectrum = applyMelFilterbank(fftData, melFilterbank)

        return dctTransform(melSpectrum.map { log(it.coerceAtLeast(1.0E-10F)) }, 40)
    }

    private fun calculateMFCCFeatures(mfccs: Array<FloatArray>): FloatArray {
        val features = mutableListOf<Float>()

        for (i in mfccs.indices) {
            val stats = DescriptiveStatistics()
            mfccs[i].forEach { stats.addValue(it.toDouble()) }

            features.add(stats.mean.toFloat())
            features.add(stats.standardDeviation.toFloat())
            features.add(stats.max.toFloat())
            features.add(stats.min.toFloat())
            features.add(calculateEntropy(mfccs[i]))
            features.add(stats.getPercentile(50.0).toFloat())
            features.add(stats.variance.toFloat())
            features.add(calculateSkewness(mfccs[i]))
            features.add(stats.getPercentile(25.0).toFloat())
            features.add(stats.getPercentile(75.0).toFloat())
            features.add((stats.getPercentile(75.0) - stats.getPercentile(25.0)).toFloat())
            features.add((stats.max - stats.min).toFloat())
            features.add(calculateKurtosis(mfccs[i]))
        }

        return features.toFloatArray()
    }

    private fun calculateWaveletFeatures(audioData: FloatArray): FloatArray {
        return WaveletTransform.calculateWaveletFeatures(audioData, 4)

//        val wavelet = Wavelet(audioData, "db1")
//        val coeffs = wavelet.getCoefficients(4)
//
//        val features = mutableListOf<Float>()
//        coeffs.forEach { coeff ->
//            if (coeff.isNotEmpty()) {
//                val stats = DescriptiveStatistics()
//                coeff.forEach { stats.addValue(it.toDouble()) }
//
//                features.add(stats.mean.toFloat())
//                features.add(stats.standardDeviation.toFloat())
//                features.add(stats.max.toFloat())
//                features.add(stats.min.toFloat())
//            }
//        }
//
//        // Calculate additional statistics for all wavelet features
//        val allWaveletStats = DescriptiveStatistics()
//        features.forEach { allWaveletStats.addValue(it.toDouble()) }
//
//        features.add(allWaveletStats.mean.toFloat())
//        features.add(allWaveletStats.standardDeviation.toFloat())
//        features.add(allWaveletStats.max.toFloat())
//        features.add(allWaveletStats.min.toFloat())
//        features.add(allWaveletStats.getPercentile(50.0).toFloat())
//        features.add(allWaveletStats.variance.toFloat())
//        features.add(calculateSkewness(features.toFloatArray()))
//        features.add(allWaveletStats.getPercentile(25.0).toFloat())
//        features.add(allWaveletStats.getPercentile(75.0).toFloat())
//        features.add((allWaveletStats.getPercentile(75.0) - allWaveletStats.getPercentile(25.0)).toFloat())
//        features.add((allWaveletStats.max - allWaveletStats.min).toFloat())
//        features.add(calculateKurtosis(features.toFloatArray()))
//
//        return features.toFloatArray()
    }

    // Helper functions

    private fun createMelFilterbank(numFilters: Int, fftSize: Int, sampleRate: Int): Array<FloatArray> {
        val lowFreq = 0f
        val highFreq = sampleRate / 2f
        val melLow = 1127f * log((1f + lowFreq / 700f))
        val melHigh = 1127f * log((1f + highFreq / 700f))
        val melPoints = LinearSpacedArray(numFilters + 2, melLow, melHigh)
        val hzPoints = melPoints.map { 700f * (exp(it / 1127f) - 1f) }
        val bins = hzPoints.map { (it * fftSize / sampleRate).roundToInt() }

        return Array(numFilters) { i ->
            FloatArray(fftSize / 2 + 1) { j ->
                when {
                    j < bins[i] -> 0f
                    j < bins[i + 1] -> (j - bins[i]) / (bins[i + 1] - bins[i]).toFloat()
                    j < bins[i + 2] -> (bins[i + 2] - j) / (bins[i + 2] - bins[i + 1]).toFloat()
                    else -> 0f
                }
            }
        }
    }

//    private fun log(value: Float, base: Float): Float {
//        return (kotlin.math.log(value.toDouble()) / kotlin.math.log(base.toDouble())).toFloat()
//    }

//    private fun applyMelFilterbank(fftData: FloatArray, melFilterbank: Array<FloatArray>): FloatArray {
//        return melFilterbank.map { filter ->
//            filter.mapIndexed { index, value ->
//                value * (fftData[index * 2].pow(2) + fftData[index * 2 + 1].pow(2))
//            }.sum()
//        }.toFloatArray()
//    }
private fun applyMelFilterbank(fftData: FloatArray, melFilterbank: Array<FloatArray>): FloatArray {
    return melFilterbank.map { filter ->
        filter.mapIndexed { index, value ->
            if (index * 2 < fftData.size) {
                value * (fftData[index * 2].pow(2) + fftData.getOrElse(index * 2 + 1) { 0f }.pow(2))
            } else {
                0f
            }
        }.sum()
    }.toFloatArray()
}

    private fun dctTransform(input: List<Float>, numCoefficients: Int): Array<FloatArray> {
        val output = Array(numCoefficients) { FloatArray(input.size) }
        for (k in 0 until numCoefficients) {
            for (n in input.indices) {
                output[k][n] = (input[n] * cos(PI / input.size * (n + 0.5) * k)).toFloat()
            }
        }
        return output
    }

    private fun calculateEntropy(data: FloatArray): Float {
        val histogram = data.groupBy { it }.mapValues { it.value.size.toFloat() / data.size }
        return -histogram.values.sumOf { (it * log2(it)).toDouble() }.toFloat()
    }

    private fun calculateSkewness(data: FloatArray): Float {
        val mean = data.average()
        val variance = data.map { (it - mean).pow(2) }.average()
        val std = sqrt(variance)
        return data.map { ((it - mean) / std).pow(3) }.average().toFloat()
    }

    private fun calculateKurtosis(data: FloatArray): Float {
        val mean = data.average()
        val variance = data.map { (it - mean).pow(2) }.average()
        val std = sqrt(variance)
        return data.map { ((it - mean) / std).pow(4) }.average().toFloat() - 3f
    }

    private fun LinearSpacedArray(size: Int, start: Float, end: Float): FloatArray {
        return FloatArray(size) { i -> (start + i * (end - start) / (size - 1)).toFloat() }
    }
}

// Usage in your ViewModel or wherever appropriate
fun extractFeatures(context: Context, wavFilePath: String): FloatArray {
    val extractor = AudioFeatureExtractor()
    return extractor.extractFeatures(context, wavFilePath)
}
//class FeatureExtractor {
//    companion object {
//        private const val SAMPLE_RATE = 44100
//        private const val RECORD_DURATION_SEC = 5
//    }
//
//    fun extractFeaturesFromWav(filePath: String): FloatArray {
//        val audioData = loadWavFile(filePath)
//        return extractFeatures(audioData)
//    }
//
//    private fun loadWavFile(filePath: String): FloatArray {
//        // Implement WAV file loading here
//        // This is a placeholder and needs to be implemented
//        return FloatArray(SAMPLE_RATE * RECORD_DURATION_SEC)
//    }
//
//    fun extractFeatures(audioData: FloatArray): FloatArray {
//        val mfcc = computeMFCC(audioData)
//        val entropy = computeShannon(audioData)
//        val wavelet = computeWavelet(audioData)
//
//        val features = mutableListOf<Float>()
//
//        // MFCC features
//        features.add(mfcc.average().toFloat())
//        features.add(mfcc.standardDeviation())
//        features.add(mfcc.maxOrNull() ?: 0f)
//        features.add(mfcc.minOrNull() ?: 0f)
//        features.add(mfcc.median())
//        features.add(mfcc.variance())
//        features.add(Skewness().evaluate(mfcc.toDoubleArray()).toFloat())
//        val q1 = mfcc.percentile(25f)
//        val q3 = mfcc.percentile(75f)
//        features.add(q1)
//        features.add(q3)
//        features.add(q3 - q1) // IQR
//        features.add(mfcc.maxOrNull()!! - mfcc.minOrNull()!!) // Range
//        features.add(Kurtosis().evaluate(mfcc.toDoubleArray()).toFloat())
//
//        // Entropy feature
//        features.add(entropy)
//
//        // Wavelet features
//        features.add(wavelet.average().toFloat())
//        features.add(wavelet.standardDeviation())
//        features.add(wavelet.maxOrNull() ?: 0f)
//        features.add(wavelet.minOrNull() ?: 0f)
//        features.add(wavelet.median())
//        features.add(wavelet.variance())
//        features.add(Skewness().evaluate(wavelet.toDoubleArray()).toFloat())
//        val wq1 = wavelet.percentile(25f)
//        val wq3 = wavelet.percentile(75f)
//        features.add(wq1)
//        features.add(wq3)
//        features.add(wq3 - wq1) // IQR
//        features.add(wavelet.maxOrNull()!! - wavelet.minOrNull()!!) // Range
//        features.add(Kurtosis().evaluate(wavelet.toDoubleArray()).toFloat())
//
//        return features.toFloatArray()
//    }
//
//    private fun computeMFCC(audioData: FloatArray): FloatArray {
//        // Implement MFCC computation here
//        // This is a placeholder and needs to be implemented
//        return FloatArray(13) { 0f }
//    }
//
//    private fun computeShannon(audioData: FloatArray): Float {
//        val histogram = audioData.groupBy { it }.mapValues { it.value.size.toFloat() / audioData.size }
//        return -histogram.values.sumOf { it * log2(it) }.toFloat()
//    }
//
//    private fun computeWavelet(audioData: FloatArray): FloatArray {
//        // Implement Wavelet computation here
//        // This is a placeholder and needs to be implemented
//        return FloatArray(audioData.size) { 0f }
//    }
//
//    private fun FloatArray.standardDeviation() =
//        kotlin.math.sqrt(this.map { (it - this.average()).pow(2) }.average()).toFloat()
//
//    private fun FloatArray.variance() =
//        this.map { (it - this.average()).pow(2) }.average().toFloat()
//
//    private fun FloatArray.median() =
//        this.sorted().let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }
//
//    private fun FloatArray.percentile(percentile: Float): Float {
//        val sorted = this.sorted()
//        val index = (percentile / 100 * (sorted.size - 1)).toInt()
//        return sorted[index]
//    }
//}