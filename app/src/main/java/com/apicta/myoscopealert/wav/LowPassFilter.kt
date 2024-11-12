package com.apicta.myoscopealert.wav

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class AudioLowPassFilter {
    companion object {
        private const val WAV_HEADER_SIZE = 44  // Standard WAV header size

        /**
         * Applies low-pass filtering to WAV file data
         * @param wavData Complete WAV file byte array (including header)
         * @param sampleRate Audio sample rate
         * @param cutoffFreq Cutoff frequency in Hz
         * @param order Filter order (must be even)
         * @return Filtered WAV file byte array
         */
        fun applyLowPassFilter(
            wavData: ByteArray,
            sampleRate: Int,
            cutoffFreq: Double,
            order: Int
        ): ByteArray {
            // Separate header and audio data
            val header = wavData.slice(0 until WAV_HEADER_SIZE).toByteArray()
            val audioData = wavData.slice(WAV_HEADER_SIZE until wavData.size).toByteArray()

            // Convert audio byte array to float array
            val floatArray = ByteArray2FloatArray(audioData)

            // Generate and apply filter
            val coefficients = createLowPassFIR(order, cutoffFreq, sampleRate)
            val filteredFloat = applyFIRFilter(floatArray, coefficients)

            // Convert filtered data back to bytes
            val filteredAudioBytes = FloatArray2ByteArray(filteredFloat)

            // Combine header with filtered audio data
            return header + filteredAudioBytes
        }

        /**
         * Convert byte array to float array
         */
        private fun ByteArray2FloatArray(audioData: ByteArray): FloatArray {
            val shorts = ShortArray(audioData.size / 2)
            ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts)

            return FloatArray(shorts.size) { shorts[it].toFloat() / Short.MAX_VALUE }
        }

        /**
         * Convert float array back to byte array
         */
        private fun FloatArray2ByteArray(floatArray: FloatArray): ByteArray {
            val shorts = ShortArray(floatArray.size) {
                (floatArray[it] * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val bytes = ByteArray(shorts.size * 2)
            val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
            shorts.forEach { buffer.putShort(it) }

            return bytes
        }

        /**
         * Creates FIR filter coefficients using Hamming window
         */
        private fun createLowPassFIR(order: Int, cutoffFreq: Double, sampleRate: Int): DoubleArray {
            val coefficients = DoubleArray(order + 1)
            val fc = cutoffFreq / sampleRate

            for (i in 0..order) {
                coefficients[i] = if (i == order / 2) {
                    2.0 * fc
                } else {
                    sin(2.0 * PI * fc * (i - order / 2)) / (i - order / 2) *
                            (0.54 - 0.46 * cos(2.0 * PI * i / order))
                }
            }

            // Normalize coefficients
            val sum = coefficients.sum()
            for (i in coefficients.indices) {
                coefficients[i] /= sum
            }

            return coefficients
        }

        /**
         * Applies FIR filter to float array using given coefficients
         */
        private fun applyFIRFilter(input: FloatArray, coefficients: DoubleArray): FloatArray {
            val output = FloatArray(input.size)
            val buffer = DoubleArray(coefficients.size)
            var bufferIndex = 0

            for (i in input.indices) {
                buffer[bufferIndex] = input[i].toDouble()
                var filteredValue = 0.0

                var index = bufferIndex
                for (j in coefficients.indices) {
                    filteredValue += coefficients[j] * buffer[index]
                    index = if (index == 0) buffer.size - 1 else index - 1
                }

                output[i] = filteredValue.toFloat()
                bufferIndex = (bufferIndex + 1) % buffer.size
            }

            return output
        }
    }
}