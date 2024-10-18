package com.apicta.myoscopealert.ai

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apicta.myoscopealert.readWavFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.jtransforms.fft.FloatFFT_1D
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.pow

class HeartbeatViewModel(application: Application) : AndroidViewModel(application) {
    private val predictor = HeartbeatPredictor(application)
    private val _prediction = MutableStateFlow<String?>(null)
    val prediction: StateFlow<String?> = _prediction.asStateFlow()
    private val featureExtractor = AudioFeatureExtractor()

    fun predictHeartbeatFromPath(wavFilePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(wavFilePath)
                val features = featureExtractor.extractFeatures(getApplication(), wavFilePath)
                Log.d("HeartbeatViewModel", "features size: ${features.size}")
                Log.d("HeartbeatViewModel", "Extracted features: ${features.contentToString()}")

                var result = predictor.predict(features)
                Log.d("HeartbeatViewModel", "Prediction result1: $result")
                if (result in 0f..0.1f) result *= 10
                Log.d("HeartbeatViewModel", "Prediction result2: $result")
                _prediction.value = if (result in 0.6f..1f) "MI Detected" else "Normal"
                Log.d("HeartbeatViewModel", "Final prediction: ${_prediction.value}")
            } catch (e: Exception) {
                Log.e("HeartbeatViewModel", "Failed to predict heartbeat from WAV path", e)
                _prediction.value = "Error"
            }
        }
    }
//    // Fungsi baru untuk memprediksi heartbeat dari path WAV
//    fun predictHeartbeatFromPath(wavFilePath: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val audioData = readWavFile(wavFilePath)
//                Log.d("HeartbeatViewModel", "Audio data berhasil dibaca: ${audioData.size} sampel")
//
//                viewModelScope.launch(Dispatchers.Default) {
//                    try {
//                        val features = extractFeatures(audioData)
//                        Log.d("HeartbeatViewModel", "features size: ${features.size}")
//                        Log.d("HeartbeatViewModel", "Extracted features: ${features.contentToString()}")
////                        val standardizedFeatures = standardizePerFeature(features)
//
//                        val result = predictor.predict(features)
//                        Log.d("HeartbeatViewModel", "Prediction result: $result")
//
//                        _prediction.value = if (result >= 1f || result <= 0.0001f) "Normal" else "MI"
////                        _prediction.value = if (result >= 0.94f) "Normal" else "MI"
//                        Log.e("Heartbeatvm", _prediction.value.toString())
//
//                        Log.d("HeartbeatViewModel", "Final prediction: ${_prediction.value}")
//                    } finally {
//                        // Bersihkan referensi audioData setelah diproses
//                        audioData.fill(0f)
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("HeartbeatViewModel", "Failed to predict heartbeat from WAV path", e)
//                _prediction.value = "Error"
//                Log.e("Heartbeatvm", _prediction.value.toString())
//
//            }
//        }
//    }
//    fun standardizePerFeature(features: FloatArray): FloatArray {
//        val standardized = FloatArray(features.size)
//        for (i in features.indices) {
//            val featureMean = features.average().toFloat()
//            val featureStdDev = Math.sqrt(features.map { (it - featureMean) * (it - featureMean) }.average()).toFloat()
//
//            if (featureStdDev != 0.0f) {
//                standardized[i] = (features[i] - featureMean) / featureStdDev
//            } else {
//                standardized[i] = features[i] // Avoid division by zero
//            }
//        }
//        return standardized
//    }
//    fun predictHeartbeat(audioData: FloatArray) {
//        viewModelScope.launch(Dispatchers.Default) {
//            val features = extractFeatures(audioData)
//            val result = predictor.predict(features)
//            Log.e("Heartbeatvm", _prediction.value.toString())
//            _prediction.value = if (result > 0.5f) "MI" else "Normal"
//            Log.e("Heartbeatvm", _prediction.value.toString())
//        }
//    }

    private fun extractFeatures(audioData: FloatArray): FloatArray {
        val features = mutableListOf<Float>()

        // MFCC features
        val mfcc = calculateMFCC(audioData)
        val mfccStats = calculateStats(mfcc)
        features.addAll(mfccStats)

        // Shannon entropy
        val entropy = calculateShannonsEntropy(audioData)
        features.add(entropy)

        // Wavelet features
        val waveletFeatures = calculateWaveletFeatures(audioData)
        val waveletStats = calculateStats(waveletFeatures)
        features.addAll(waveletStats)


        Log.d("Features", "Extracted MFCC: ${mfccStats.joinToString()}")
        Log.d("Features", "Extracted Shannon Entropy: $entropy")
        Log.d("Features", "Extracted Wavelet: ${waveletStats.joinToString()}")
        // Mengganti NaN dengan 0
        return features.map { if (it.isNaN()) 0f else it }.toFloatArray()
    }

    private fun calculateMFCC(audioData: FloatArray, sampleRate: Int = 44100, numCoefficients: Int = 13): FloatArray {
        // Terapkan windowing
        val windowedAudio = applyHammingWindow(audioData)

        // Pad input ke power of 2
        val paddedSize = 2.0.pow(ceil(log2(audioData.size.toDouble())).toInt()).toInt()
        val paddedAudio = windowedAudio.copyOf(paddedSize)

        // Lakukan FFT
        val fft = FloatFFT_1D(paddedSize.toLong())
        val fftData = paddedAudio.copyOf()
        fft.realForward(fftData)

        // Konversi FFT output ke spektrum
        val spectrum = DoubleArray(paddedSize / 2 + 1)
        spectrum[0] = fftData[0].toDouble() // DC component
        for (i in 1 until paddedSize / 2) {
            spectrum[i] = Math.hypot(fftData[2 * i].toDouble(), fftData[2 * i + 1].toDouble())
        }
        spectrum[paddedSize / 2] = fftData[1].toDouble() // Nyquist frequency

        // Hitung mel filterbank
        val melFilterbank = createMelFilterbank(paddedSize, sampleRate, 13)

        // Terapkan mel filterbank dan ambil log
        val melSpectrum = applyMelFilterbank(spectrum, melFilterbank)
        for (i in melSpectrum.indices) {
            melSpectrum[i] = ln(melSpectrum[i].coerceAtLeast(1e-10)) / ln(2.0) // Log2
        }

        // Terapkan DCT
        val mfcc = applyDCT(melSpectrum)

        // Ambil hanya koefisien yang diinginkan
        return mfcc.take(numCoefficients).map { it.toFloat() }.toFloatArray()
    }


    //    private fun calculateMFCC(audioData: FloatArray): FloatArray {
//        // Terapkan windowing
//        val windowedAudio = applyHammingWindow(audioData)
//
//        // Pad input ke power of 2
//        val paddedSize = 2.0.pow(ceil(log2(audioData.size.toDouble())).toInt()).toInt()
//        val paddedAudio = windowedAudio.copyOf(paddedSize)
//
//        // Lakukan FFT
//        val fft = FloatFFT_1D(paddedSize.toLong())
//        val fftData = paddedAudio.copyOf()
//        fft.realForward(fftData)
//
//        // Konversi FFT output ke spektrum
//        val spectrum = DoubleArray(paddedSize / 2 + 1)
//        spectrum[0] = fftData[0].toDouble() // DC component
//        for (i in 1 until paddedSize / 2) {
//            spectrum[i] = Math.hypot(fftData[2 * i].toDouble(), fftData[2 * i + 1].toDouble()).toDouble()
//        }
//        spectrum[paddedSize / 2] = fftData[1].toDouble() // Nyquist frequency
//
//        // Hitung mel filterbank
//        val melFilterbank = createMelFilterbank(paddedSize, 44100, 13)
//
//        // Terapkan mel filterbank dan ambil log
//        val melSpectrum = applyMelFilterbank(spectrum, melFilterbank)
//        for (i in melSpectrum.indices) {
//            melSpectrum[i] = ln(melSpectrum[i].coerceAtLeast(1e-10)) / ln(2.0) // Log2
//        }
//
//        // Terapkan DCT
//        val mfcc = applyDCT(melSpectrum)
//
//        return mfcc.map { it.toFloat() }.toFloatArray()
//    }
    private fun applyHammingWindow(data: FloatArray): FloatArray {
        return data.mapIndexed { i, sample ->
            sample * (0.54 - 0.46 * cos(2 * PI * i / (data.size - 1))).toFloat()
        }.toFloatArray()
    }
//    private fun calculateMFCC(audioData: FloatArray): FloatArray {
//        // Pad the input to the nearest power of 2
//        val paddedSize = 2.0.pow(ceil(log2(audioData.size.toDouble()))).toInt()
//        val paddedAudio = audioData.copyOf(paddedSize)
//
//        val fft = FastFourierTransformer(DftNormalization.STANDARD)
//        val spectrum = fft.transform(paddedAudio.map { it.toDouble() }.toDoubleArray(), TransformType.FORWARD)
//
//        // Calculate mel-scale filterbank
//        val melFilterbank = createMelFilterbank(paddedSize, 44100, 13)
//
//        // Apply mel filterbank and take log
//        val melSpectrum = applyMelFilterbank(spectrum.map { it.abs() }.toDoubleArray(), melFilterbank)
//
//        // Apply DCT
//        val mfcc = applyDCT(melSpectrum.map { log2(it.coerceAtLeast(1e-10)) }.toDoubleArray())
//
//        return mfcc.map { it.toFloat() }.toFloatArray()
//    }

    private fun calculateShannonsEntropy(audioData: FloatArray): Float {
        val histogram = audioData.groupBy { it }.mapValues { it.value.size.toDouble() / audioData.size }
        return -histogram.values.sumOf { if (it > 0) it * log2(it) else 0.0 }.toFloat()
    }

    private fun calculateWaveletFeatures(audioData: FloatArray): FloatArray {
        // This is a simplified wavelet transform
        // For a more accurate implementation, consider using a dedicated wavelet library
        val level = 4
        var approximation = audioData
        val details = mutableListOf<FloatArray>()

        repeat(level) {
            val (a, d) = decompose(approximation)
            approximation = a
            details.add(d)
        }

//        return (listOf(approximation) + details.reversed()).flatten().toFloatArray()
        return (listOf(approximation) + details.reversed()).flatMap { it.toList() }.toFloatArray()

    }

    private fun decompose(data: FloatArray): Pair<FloatArray, FloatArray> {
        val n = data.size
        val a = FloatArray(n / 2) { (data[it * 2] + data[it * 2 + 1]) / 2f }
        val d = FloatArray(n / 2) { (data[it * 2] - data[it * 2 + 1]) / 2f }
        return Pair(a, d)
    }

    private fun calculateStats(data: FloatArray): List<Float> {
        val stats = DescriptiveStatistics(data.map { it.toDouble() }.toDoubleArray())
        return listOf(
            stats.mean.toFloat(),
            stats.standardDeviation.toFloat(),
            stats.max.toFloat(),
            stats.min.toFloat(),
            stats.getPercentile(50.0).toFloat(),
            stats.variance.toFloat(),
            stats.skewness.toFloat(),
            stats.getPercentile(25.0).toFloat(),
            stats.getPercentile(75.0).toFloat(),
            (stats.getPercentile(75.0) - stats.getPercentile(25.0)).toFloat(),
            (stats.max - stats.min).toFloat(),
            stats.kurtosis.toFloat()
        )
    }

    private fun createMelFilterbank(nfft: Int, sampleRate: Int, nFilters: Int): Array<DoubleArray> {
        val melMin = 0.0
        val melMax = 2595.0 * ln(1.0 + (sampleRate / 2.0) / 700.0) / ln(10.0)

        val melPoints = DoubleArray(nFilters + 2) { melMin + it * (melMax - melMin) / (nFilters + 1) }
        val hzPoints = melPoints.map { 700.0 * (10.0.pow(it / 2595.0) - 1.0) }.toDoubleArray()

        val bin = hzPoints.map { floor((nfft + 1) * it / sampleRate).toInt() }.toIntArray()

        val filterbank = Array(nFilters) { DoubleArray(nfft / 2 + 1) { 0.0 } }

        for (m in 1..nFilters) {
            val f_m = bin[m]
            val f_m1 = bin[m - 1]
            val f_m2 = bin[m + 1]

            for (k in f_m1 until f_m) {
                filterbank[m - 1][k] = (k - bin[m - 1]).toDouble() / (bin[m] - bin[m - 1])
            }
            for (k in f_m until f_m2) {
                filterbank[m - 1][k] = (bin[m + 1] - k).toDouble() / (bin[m + 1] - bin[m])
            }
        }

        return filterbank
    }


//    private fun applyMelFilterbank(spectrum: DoubleArray, filterbank: Array<DoubleArray>): DoubleArray {
//        return filterbank.map { filter -> spectrum.zip(filter).sumOf { it.first * it.second } }.toDoubleArray()
//    }
    private fun applyMelFilterbank(spectrum: DoubleArray, filterbank: Array<DoubleArray>): DoubleArray {
        val melSpectrum = DoubleArray(filterbank.size)
        for (i in filterbank.indices) {
            var sum = 0.0
            val filter = filterbank[i]
            for (j in spectrum.indices) {
                sum += filter[j] * spectrum[j]
            }
            melSpectrum[i] = sum
        }
        return melSpectrum
    }

    private fun applyDCT(input: DoubleArray): DoubleArray {
        val n = input.size
        val dct = DoubleArray(n)
        for (k in 0 until n) {
            var sum = 0.0
            for (i in 0 until n) {
                sum += input[i] * cos(Math.PI * k * (2 * i + 1) / (2.0 * n))
            }
            dct[k] = sum
        }
        return dct
    }
    override fun onCleared() {
        super.onCleared()
        predictor.close()
    }
}



//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.viewModelScope
//import be.tarsos.dsp.AudioEvent
//import be.tarsos.dsp.AudioProcessor
//import be.tarsos.dsp.ZeroCrossingRateProcessor
//import be.tarsos.dsp.mfcc.MFCC
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//
//class HeartbeatViewModel(application: Application) : AndroidViewModel(application) {
//    private val predictor = HeartbeatPredictor(application)
//    private val _prediction = MutableLiveData<String>()
//    val prediction: LiveData<String> = _prediction
//
//    // Konstanta untuk ekstraksi fitur
//    private val SAMPLE_RATE = 44100.0
//    private val BUFFER_SIZE = 1024
//    private val OVERLAP = 512
//    private val MFCC_COEFFICIENTS = 13
//
//    fun predictHeartbeat(audioData: FloatArray) {
//        viewModelScope.launch(Dispatchers.Default) {
//            val features = extractFeatures(audioData)
//            val result = predictor.predict(features)
//            _prediction.postValue(if (result > 0.5f) "MI" else "Normal")
//        }
//    }
//
//    private fun extractFeatures(audioData: FloatArray): FloatArray {
//        val mfcc = MFCC(
//            bufferSize = BUFFER_SIZE,
//            sampleRate = SAMPLE_RATE.toFloat(),
//            numCepstra = MFCC_COEFFICIENTS,
//            numMelFilters = 26,
//            lowerFrequency = 20.0f,
//            upperFrequency = 20000.0f
//        )
//
//        val zcrProcessor = ZeroCrossingRateProcessor()
//
//        val featureList = mutableListOf<Float>()
//
//        // Buat AudioProcessor untuk MFCC dan ZCR
//        val mfccProcessor = object : AudioProcessor {
//            override fun process(audioEvent: AudioEvent): Boolean {
//                mfcc.process(audioEvent)
//                val mfccs = mfcc.mfcc.clone()
//                featureList.addAll(mfccs.toList())
//                return true
//            }
//
//            override fun processingFinished() {}
//        }
//
//        val zcrProcessorCustom = object : AudioProcessor {
//            override fun process(audioEvent: AudioEvent): Boolean {
//                zcrProcessor.process(audioEvent)
//                val zcr = zcrProcessor.zcr
//                featureList.add(zcr)
//                return true
//            }
//
//            override fun processingFinished() {}
//        }
//
//        // Proses audio data
//        val audioProcessorList = listOf(mfccProcessor, zcrProcessorCustom)
//
//        val audioEvent = AudioEvent(44100.0, audioData.size.toLong())
//        audioEvent.floatBuffer = audioData
//
//        // Simulasi pemrosesan frame audio
//        var position = 0
//        while (position + BUFFER_SIZE <= audioData.size) {
//            val buffer = audioData.copyOfRange(position, position + BUFFER_SIZE)
//            audioEvent.floatBuffer = buffer
//            audioProcessorList.forEach { it.process(audioEvent) }
//            position += OVERLAP
//        }
//
//        // Setelah pemrosesan, kita dapat menambahkan RMS
//        val rms = calculateRMS(audioData)
//        featureList.add(rms)
//
//        // Jika fitur kurang dari 25, padding dengan nol
//        while (featureList.size < 25) {
//            featureList.add(0.0f)
//        }
//
//        // Jika fitur lebih dari 25, potong sesuai kebutuhan
//        if (featureList.size > 25) {
//            return featureList.subList(0, 25).toFloatArray()
//        }
//
//        return featureList.toFloatArray()
//    }
//
//    private fun calculateRMS(audioData: FloatArray): Float {
//        var sum = 0.0
//        for (sample in audioData) {
//            sum += sample * sample
//        }
//        return kotlin.math.sqrt(sum / audioData.size)
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        predictor.close()
//    }
//}
