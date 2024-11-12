package com.apicta.myoscopealert.utils

import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import be.tarsos.dsp.io.jvm.WaveformWriter
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.TarsosDSPAudioFormat
//import be.tarsos.dsp.io.TarsosDSPAudioFormat
//import javax.sound.sampled.AudioFormat
import java.io.File

// Define the function to apply low-pass filtering
//fun applyLowPassFilter(inputFile: File, outputFile: File) {
//    val dispatcher = AudioDispatcherFactory.fromFile(inputFile, 512, 256)
//
//    val sampleRate = 1000 // Adjust based on your file
//    val order = 2
//    val cutoffFreq = 3.0
//
//    val firCoefficients = createLowPassFIR(order, cutoffFreq, sampleRate)
//    dispatcher.addAudioProcessor(FIRFilterProcessor(firCoefficients))
//
//    val audioFormat = AudioFormat(
//        24000f, 8, 1, true, false
//    )
//
//    val writer = WaveformWriter(audioFormat, outputFile.absolutePath)
//    dispatcher.addAudioProcessor(writer)
//
//    dispatcher.run()
//}
fun applyLowPassFilter(inputFile: File, outputFile: File) {
    val dispatcher = AudioDispatcherFactory.fromFile(inputFile, 512, 256)
//    val dispatcher = AudioDispatcherFactory.fromFile(inputFile, 512, 256)

    val sampleRate = 1000 // Adjust based on your file
    val order = 2
    val cutoffFreq = 3.0

    val firCoefficients = createLowPassFIR(order, cutoffFreq, sampleRate)
    dispatcher.addAudioProcessor(FIRFilterProcessor(firCoefficients))

    val audioFormat = TarsosDSPAudioFormat(
//    val audioFormat = AudioFormat(
        24000f, 8, 1, true, false
    )

    val writer = WaveformWriter(audioFormat, outputFile.absolutePath)
    dispatcher.addAudioProcessor(writer)

    dispatcher.run()
}

// Create FIR filter coefficients
fun createLowPassFIR(order: Int, cutoffFreq: Double, sampleRate: Int): DoubleArray {
    val coefficients = DoubleArray(order + 1)
    val fc = cutoffFreq / sampleRate

    for (i in 0..order) {
        coefficients[i] = if (i == order / 2) {
            2 * Math.PI * fc
        } else {
            Math.sin(2 * Math.PI * fc * (i - order / 2)) / (i - order / 2) *
                    (0.54 - 0.46 * Math.cos(2 * Math.PI * i / order))
        }
    }
    return coefficients
}

// FIR Filter processor for TarsosDSP
class FIRFilterProcessor(private val coefficients: DoubleArray) : AudioProcessor {
    private val buffer = DoubleArray(coefficients.size)
    private var bufferIndex = 0

    override fun process(audioEvent: AudioEvent): Boolean {
        val audioBuffer = audioEvent.floatBuffer
        for (i in audioBuffer.indices) {
            buffer[bufferIndex] = audioBuffer[i].toDouble()
            var output = 0.0

            var index = bufferIndex
            for (j in coefficients.indices) {
                output += coefficients[j] * buffer[index]
                index = if (index == 0) buffer.size - 1 else index - 1
            }

            audioBuffer[i] = output.toFloat()
            bufferIndex = (bufferIndex + 1) % buffer.size
        }
        return true
    }

    override fun processingFinished() {}
}