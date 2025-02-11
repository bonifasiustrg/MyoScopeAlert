package com.apicta.myoscopealert.wav

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

///**
// * Reads a WAV file and converts PCM data to FloatArray.
// *
// * @param filePath Full path to the WAV file.
// * @return FloatArray containing normalized audio data.
// * @throws IllegalArgumentException If the WAV format is not supported or a read error occurs.
// */
//fun readWavFile(filePath: String, context: Context, maxDurationSeconds: Double = 2.5, targetSampleRate: Int = 22050): FloatArray {
//    val file = File(filePath)
//    if (!file.exists()) {
//        throw IllegalArgumentException("File not found: $filePath")
//    }
//
//    FileInputStream(file).use { fis ->
//        BufferedInputStream(fis).use { bis ->
//            val header = ByteArray(44)
//            val bytesRead = bis.read(header, 0, 44)
//            if (bytesRead < 44) {
//                throw IllegalArgumentException("Incomplete WAV header.")
//            }
//
//            // Parse important information from the header
//            val byteOrder = ByteOrder.LITTLE_ENDIAN
//            val audioFormat = ByteBuffer.wrap(header, 20, 2).order(byteOrder).short
//            val isPcm = audioFormat == 1.toShort()
//            val isPcmFloat = audioFormat == 3.toShort()
//            if (!isPcm && !isPcmFloat) { // 1 = PCM, 3 = IEEE Float
//                throw IllegalArgumentException("Unsupported audio format: $audioFormat")
//            }
//
//            val numChannels = ByteBuffer.wrap(header, 22, 2).order(byteOrder).short.toInt()
//            val sampleRate = ByteBuffer.wrap(header, 24, 4).order(byteOrder).int
//            val bitsPerSample = ByteBuffer.wrap(header, 34, 2).order(byteOrder).short.toInt()
//
//            // Determine the number of bytes per sample
//            val bytesPerSample = bitsPerSample / 8
//
//            // Calculate the maximum number of samples to be processed
//            val maxSamples = (targetSampleRate * maxDurationSeconds).toInt()
//
//            // Read PCM data with limits
//            val pcmData = ByteArray((maxSamples * numChannels * bytesPerSample).coerceAtMost(bis.available()))
//            bis.read(pcmData, 0, pcmData.size)
//
//            // Convert PCM to FloatArray
//            val numSamples = pcmData.size / bytesPerSample / numChannels
//            val audioData = FloatArray(numSamples)
//
//            for (i in 0 until numSamples) {
//                var sum = 0f
//                for (ch in 0 until numChannels) {
//                    val sampleIndex = (i * numChannels + ch) * bytesPerSample
//                    val sample: Float = when {
//                        isPcmFloat && bitsPerSample == 32 -> {
//                            // 32-bit PCM Float
//                            ByteBuffer.wrap(pcmData, sampleIndex, 4).order(byteOrder).float
//                        }
//                        isPcm && bitsPerSample == 8 -> {
//                            // 8-bit PCM (Unsigned)
//                            val unsignedByte = pcmData[sampleIndex].toInt() and 0xFF
//                            (unsignedByte - 128) / 128f
//                        }
//                        isPcm && bitsPerSample == 16 -> {
//                            // 16-bit PCM (Signed)
//                            val shortVal = ByteBuffer.wrap(pcmData, sampleIndex, 2).order(byteOrder).short
//                            shortVal / 32768f
//                        }
//                        isPcm && bitsPerSample == 32 -> {
//                            // 32-bit PCM (Signed)
//                            val intVal = ByteBuffer.wrap(pcmData, sampleIndex, 4).order(byteOrder).int
//                            intVal / 2147483648f
//                        }
//                        else -> throw IllegalArgumentException("Unsupported bits per sample or unrecognized format: $bitsPerSample, $audioFormat")
//                    }
//                    sum += sample
//                }
//                // Combine channels (average for mono)
//                audioData[i] = sum / numChannels
//            }
//
//            // Resample to target sample rate if necessary
//            if (sampleRate != targetSampleRate) {
//                val resampledData = resample(audioData, sampleRate, targetSampleRate)
//                return resampledData
//            }
//            // Menampilkan data audio (hati-hati jika dataset besar, bisa terlalu panjang)
//            println(audioData.joinToString(", "))  // Mencetak seluruh elemen array sebagai string
//
//            // Mendapatkan direktori internal untuk menyimpan file
//            val file = File(context.filesDir, "output_audio_data.txt")
//
//            try {
//                // Menyimpan audioData ke file internal
//                file.writeText(audioData.joinToString(", "))
//                println("File saved successfully to ${file.absolutePath}")
//            } catch (e: IOException) {
//                e.printStackTrace()
//                println("Failed to write to file: ${e.message}")
//            }
//            return audioData
//        }
//    }
//}
//
///**
// * Resamples the audio data to the target sample rate.
// *
// * @param inputData The input audio data.
// * @param originalSampleRate The original sample rate of the audio data.
// * @param targetSampleRate The target sample rate.
// * @return The resampled audio data.
// */
//fun resample(inputData: FloatArray, originalSampleRate: Int, targetSampleRate: Int): FloatArray {
//    val ratio = targetSampleRate.toDouble() / originalSampleRate
//    val newLength = (inputData.size * ratio).toInt()
//    val resampledData = FloatArray(newLength)
//    for (i in resampledData.indices) {
//        val srcIndex = i / ratio
//        val srcIndexInt = srcIndex.toInt()
//        val frac = srcIndex - srcIndexInt
//        val nextIndex = (srcIndexInt + 1).coerceAtMost(inputData.size - 1)
//        resampledData[i] = ((1 - frac) * inputData[srcIndexInt] + frac * inputData[nextIndex]).toFloat()
//    }
//    return resampledData
//}


/**
 * Membaca file WAV dan mengonversi data PCM ke FloatArray.
 *
 * @param filePath Path lengkap ke file WAV.
 * @return FloatArray yang berisi data audio yang dinormalisasi.
 * @throws IllegalArgumentException Jika format WAV tidak didukung atau terjadi kesalahan pembacaan.
 */
fun readWavFile(filePath: String, context: Context, maxDurationSeconds: Int = 10): FloatArray {
    val file = File(filePath)
    if (!file.exists()) {
        throw IllegalArgumentException("File tidak ditemukan: $filePath")
    }

    FileInputStream(file).use { fis ->
        val header = ByteArray(44)
        val bytesRead = fis.read(header, 0, 44)
        if (bytesRead < 44) {
            throw IllegalArgumentException("Header WAV tidak lengkap.")
        }

        // Mengurai informasi penting dari header
        val byteOrder = ByteOrder.LITTLE_ENDIAN

//        val audioFormat = ByteBuffer.wrap(header, 20, 2).order(byteOrder).short
//        if (audioFormat != 1.toShort()) { // 1 = PCM
//            throw IllegalArgumentException("Format audio tidak didukung: $audioFormat")
//        }
        val audioFormat = ByteBuffer.wrap(header, 20, 2).order(byteOrder).short
        val isPcm = audioFormat == 1.toShort()
        val isPcmFloat = audioFormat == 3.toShort()
        if (!isPcm && !isPcmFloat) { // 1 = PCM, 3 = IEEE Float
            throw IllegalArgumentException("Format audio tidak didukung: $audioFormat")
        }

        val numChannels = ByteBuffer.wrap(header, 22, 2).order(byteOrder).short.toInt()
        val sampleRate = ByteBuffer.wrap(header, 24, 4).order(byteOrder).int
        val bitsPerSample = ByteBuffer.wrap(header, 34, 2).order(byteOrder).short.toInt()

        // Menentukan jumlah byte per sampel
        val bytesPerSample = bitsPerSample / 8

        // Hitung jumlah sampel maksimum yang akan diproses
        val maxSamples = sampleRate * maxDurationSeconds

        // Membaca data PCM dengan batasan
        val pcmData = ByteArray((maxSamples * numChannels * bytesPerSample).coerceAtMost(fis.available()))
        fis.read(pcmData, 0, pcmData.size)

        // Mengonversi PCM ke FloatArray
        val numSamples = pcmData.size / bytesPerSample / numChannels
        val audioData = FloatArray(numSamples)

        for (i in 0 until numSamples) {
            var sum = 0f
            for (ch in 0 until numChannels) {
                val sampleIndex = (i * numChannels + ch) * bytesPerSample
                var sample: Float = 0f

//                when (bitsPerSample) {
//                    8 -> {
//                        // 8-bit PCM (Unsigned)
//                        val unsignedByte = pcmData[sampleIndex].toInt() and 0xFF
//                        sample = (unsignedByte - 128) / 128f
//                    }
//                    16 -> {
//                        // 16-bit PCM (Signed)
//                        val shortVal = ByteBuffer.wrap(pcmData, sampleIndex, 2)
//                            .order(byteOrder)
//                            .short
//                        sample = shortVal / 32768f
//                    }
//                    32 -> {
//                        // 32-bit PCM (Signed)
//                        val intVal = ByteBuffer.wrap(pcmData, sampleIndex, 4)
//                            .order(byteOrder)
//                            .int
//                        sample = intVal / 2147483648f
//                    }
//                    else -> throw IllegalArgumentException("Bits per sample tidak didukung: $bitsPerSample")
//                }
                when {
                    isPcmFloat && bitsPerSample == 32 -> {
                        // 32-bit PCM Float
                        sample = ByteBuffer.wrap(pcmData, sampleIndex, 4)
                            .order(byteOrder)
                            .float
                    }
                    isPcm && bitsPerSample == 8 -> {
                        // 8-bit PCM (Unsigned)
                        val unsignedByte = pcmData[sampleIndex].toInt() and 0xFF
                        sample = (unsignedByte - 128) / 128f
                    }
                    isPcm && bitsPerSample == 16 -> {
                        // 16-bit PCM (Signed)
                        val shortVal = ByteBuffer.wrap(pcmData, sampleIndex, 2)
                            .order(byteOrder)
                            .short
                        sample = shortVal / 32768f
                    }
                    isPcm && bitsPerSample == 32 -> {
                        // 32-bit PCM (Signed)
                        val intVal = ByteBuffer.wrap(pcmData, sampleIndex, 4)
                            .order(byteOrder)
                            .int
                        sample = intVal / 2147483648f
                    }
                    else -> throw IllegalArgumentException("Bits per sample tidak didukung atau format tidak dikenali: $bitsPerSample, $audioFormat")
                }
                sum += sample
            }
            // Menggabungkan saluran (mengambil rata-rata untuk mono)
            audioData[i] = sum / numChannels
        }
        // Menampilkan data audio (hati-hati jika dataset besar, bisa terlalu panjang)
        println(audioData.joinToString(", "))  // Mencetak seluruh elemen array sebagai string

        // Mendapatkan direktori internal untuk menyimpan file
        val file = File(context.filesDir, "output_audio_data.txt")

        try {
            // Menyimpan audioData ke file internal
            file.writeText(audioData.joinToString(", "))
            println("File saved successfully to ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            println("Failed to write to file: ${e.message}")
        }

        return audioData
    }
}
