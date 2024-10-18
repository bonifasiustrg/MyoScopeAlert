package com.apicta.myoscopealert

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Membaca file WAV dan mengonversi data PCM ke FloatArray.
 *
 * @param filePath Path lengkap ke file WAV.
 * @return FloatArray yang berisi data audio yang dinormalisasi.
 * @throws IllegalArgumentException Jika format WAV tidak didukung atau terjadi kesalahan pembacaan.
 */
fun readWavFile(filePath: String, maxDurationSeconds: Int = 10): FloatArray {
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
            Log.d("readWavFile", "Sample : ${audioData}")
        return audioData
    }
}

//fun readWavFile(filePath: String): FloatArray {
//    val file = File(filePath)
//    if (!file.exists()) {
//        throw IllegalArgumentException("File tidak ditemukan: $filePath")
//    }
//
//    FileInputStream(file).use { fis ->
//        val header = ByteArray(44)
//        val bytesRead = fis.read(header, 0, 44)
//        if (bytesRead < 44) {
//            throw IllegalArgumentException("Header WAV tidak lengkap.")
//        }
//
//        // Mengurai informasi penting dari header
//        val byteOrder = ByteOrder.LITTLE_ENDIAN
//
//        val audioFormat = ByteBuffer.wrap(header, 20, 2).order(byteOrder).short
//        if (audioFormat != 1.toShort()) { // 1 = PCM
//            throw IllegalArgumentException("Format audio tidak didukung: $audioFormat")
//        }
//
//        val numChannels = ByteBuffer.wrap(header, 22, 2).order(byteOrder).short.toInt()
//        val sampleRate = ByteBuffer.wrap(header, 24, 4).order(byteOrder).int
//        val bitsPerSample = ByteBuffer.wrap(header, 34, 2).order(byteOrder).short.toInt()
//
//        // Menentukan jumlah byte per sampel
//        val bytesPerSample = bitsPerSample / 8
//
//        // Membaca sisa data PCM
//        val pcmData = fis.readBytes()
//
//        // Mengonversi PCM ke FloatArray
//        val numSamples = pcmData.size / bytesPerSample / numChannels
//        val audioData = FloatArray(numSamples)
//
//        for (i in 0 until numSamples) {
//            var sum = 0f
//            for (ch in 0 until numChannels) {
//                val sampleIndex = (i * numChannels + ch) * bytesPerSample
//                var sample: Float = 0f
//
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
//
//                sum += sample
//            }
//            // Menggabungkan saluran (mengambil rata-rata untuk mono)
//            audioData[i] = sum / numChannels
//        }
//
//        return audioData
//    }
//}
