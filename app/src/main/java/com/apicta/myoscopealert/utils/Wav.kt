package com.apicta.myoscopealert.utils

import android.media.AudioFormat
import com.apicta.myoscopealert.wav.WavFileBuilderKotlin
import com.apicta.myoscopealert.wav.WavFileBuilderKotlin.Companion.PCM_AUDIO_FORMAT
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Arrays
import kotlin.math.abs

object Wav {


    @Throws(IOException::class)
    fun generateWavHeader(audioData: ByteArray, channelConfig: Int, sampleRate: Int, audioFormat: Int): ByteArray {
        val dataLength = audioData.size
        val audioDataLength = audioData.size.toLong()
        val overallSize = audioDataLength + 36
        var bitsPerSample = when (audioFormat) {
            AudioFormat.ENCODING_PCM_8BIT -> 8
            AudioFormat.ENCODING_PCM_16BIT -> 16
            AudioFormat.ENCODING_PCM_FLOAT -> 32
            else -> throw IllegalArgumentException("Format audio tidak didukung: $audioFormat")
        }
        var numChannels = if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2

        val blockAlign = numChannels * bitsPerSample / 8

        val subchunk2Size = audioDataLength * blockAlign
        val chunkSize = 36 + subchunk2Size
        val header = ByteArray(44)

        // RIFF chunk descriptor
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()

//        // Overall size (file size - 8 bytes for RIFF and WAVE tags)
        header[4] = (overallSize and 0xffL).toByte()
        header[5] = (overallSize shr 8 and 0xffL).toByte()
        header[6] = (overallSize shr 16 and 0xffL).toByte()
        header[7] = (overallSize shr 24 and 0xffL).toByte()
        // Chunk size
//        header[4] = (chunkSize and 0xff).toByte()
//        header[5] = ((chunkSize shr 8) and 0xff).toByte()
//        header[6] = ((chunkSize shr 16) and 0xff).toByte()
//        header[7] = ((chunkSize shr 24) and 0xff).toByte()

        // WAVE chunk
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()

        // fmt sub-chunk
        header[12] = 'f'.code.toByte() // Sub-chunk identifier
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte() // Chunk size
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0

        // Audio format (PCM = 1) //diganti
        header[20] = 1
        header[21] = 0
//        // Audio format
//        val formatCode = when (audioFormat) {
//            AudioFormat.ENCODING_PCM_8BIT -> 1
//            AudioFormat.ENCODING_PCM_16BIT -> 1
//            AudioFormat.ENCODING_PCM_FLOAT -> 3
//            else -> throw IllegalArgumentException("Format audio tidak didukung: $audioFormat")
//        }
//        header[20] = (formatCode and 0xff).toByte()
//        header[21] = ((formatCode shr 8) and 0xff).toByte()

//        // Number of channels (2 = stereo)
        header[22] = (if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2).toByte()
        header[23] = 0
        // Number of channels
//        header[22] = (numChannels and 0xff).toByte()
//        header[23] = ((numChannels shr 8) and 0xff).toByte()
//        header[22] = numChannels.toByte()
//        header[23] = 0

        // Sample rate
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()

//        // Byte rate (Sample rate * Number of channels * Bits per sample / 8)
//        val byteRate = sampleRate * (if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2) * if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 2 else 1
        // Byte rate (Sample rate * Number of channels * Bits per sample / 8)

//        val byteRate = sampleRate * numChannels * bitsPerSample / 8
        val byteRate = sampleRate * (if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2) * if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 2 else 1

        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()

//        // Block align (Number of channels * Bits per sample / 8)
        header[32] = ((if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2) * if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 2 else 1).toByte()
        header[33] = 0
//
//        // Bits per sample
//        header[34] = (if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 16 else 8).toByte()
//        header[35] = 0
        // Block align (Number of channels * Bits per sample / 8)
//        header[32] = (blockAlign and 0xff).toByte()
//        header[33] = ((blockAlign shr 8) and 0xff).toByte()
//        header[32] = blockAlign.toByte()
//        header[33] = 0

        // Bits per sample
//        header[34] = (bitsPerSample and 0xff).toByte()
//        header[35] = ((bitsPerSample shr 8) and 0xff).toByte()
//        // Bits per sample
//        header[34] = bitsPerSample.toByte()
//        header[35] = 0
        // Bits per sample
        header[34] = (if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 16 else 8).toByte()
        header[35] = 0

        // data sub-chunk
        header[36] = 'd'.code.toByte() // Sub-chunk identifier
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte() // Chunk size
        // Subchunk2 size
//        header[40] = (subchunk2Size and 0xff).toByte()
//        header[41] = ((subchunk2Size shr 8) and 0xff).toByte()
//        header[42] = ((subchunk2Size shr 16) and 0xff).toByte()
//        header[43] = ((subchunk2Size shr 24) and 0xff).toByte()
        header[40] = (audioDataLength and 0xffL).toByte()
        header[41] = (audioDataLength shr 8 and 0xffL).toByte()
        header[42] = (audioDataLength shr 16 and 0xffL).toByte()
        header[43] = (audioDataLength shr 24 and 0xffL).toByte()


        val wavHeader = ByteArray(44 + audioData.size)
        System.arraycopy(header, 0, wavHeader, 0, 44)
        System.arraycopy(audioData, 0, wavHeader, 44, audioData.size)

        return wavHeader
    }
    fun adjustVolumeWithCompression(audioData: ByteArray, gain: Float, threshold: Float = 0.5f, ratio: Float = 4f): ByteArray {
        val adjustedData = ByteArray(audioData.size)
        val maxValue = Short.MAX_VALUE.toFloat()

        for (i in audioData.indices step 2) {
            val sample = (audioData[i].toInt() and 0xFF) or (audioData[i + 1].toInt() shl 8)
            var normalizedSample = sample.toFloat() / maxValue

            // Apply gain
            normalizedSample *= gain

            // Apply compression
            if (abs(normalizedSample) > threshold) {
                val excess = abs(normalizedSample) - threshold
                normalizedSample = threshold + excess / ratio
                if (sample < 0) normalizedSample *= -1
            }

            // Convert back to int and clamp
            var adjustedSample = (normalizedSample * maxValue).toInt()
            adjustedSample = adjustedSample.coerceIn(-32768, 32767)

            adjustedData[i] = adjustedSample.toByte()
            adjustedData[i + 1] = (adjustedSample shr 8).toByte()
        }
        return adjustedData
    }

    fun slowDownAudio(audioData: ByteArray, slowdownFactor: Float): ByteArray {
        val numSamples = audioData.size / 2
        val newNumSamples = (numSamples * slowdownFactor).toInt()
        val slowedData = ByteArray(newNumSamples * 2)

        for (i in 0 until newNumSamples) {
            val originalIndex = (i / slowdownFactor).toInt()
            val sample = if (originalIndex < numSamples) {
                (audioData[originalIndex * 2].toInt() and 0xFF) or (audioData[originalIndex * 2 + 1].toInt() shl 8)
            } else {
                0 // Padding dengan silence jika melebihi data asli
            }

            slowedData[i * 2] = sample.toByte()
            slowedData[i * 2 + 1] = (sample shr 8).toByte()
        }

        return slowedData
    }

//    @Throws(IOException::class)
//    fun writeWavHeader(outputStream: FileOutputStream, channelConfig: Int, sampleRate: Int, audioFormat: Int) {
//        val audioDataLength = outputStream.channel.size() - 44 // Subtract header size
//        val overallSize = audioDataLength + 36 // Add header size
//        val header = ByteArray(44)
//
//        // RIFF chunk descriptor
//        header[0] = 'R'.code.toByte()
//        header[1] = 'I'.code.toByte()
//        header[2] = 'F'.code.toByte()
//        header[3] = 'F'.code.toByte()
//
//        // Overall size (file size - 8 bytes for RIFF and WAVE tags)
//        header[4] = (overallSize and 0xffL).toByte()
//        header[5] = (overallSize shr 8 and 0xffL).toByte()
//        header[6] = (overallSize shr 16 and 0xffL).toByte()
//        header[7] = (overallSize shr 24 and 0xffL).toByte()
//
//        // WAVE chunk
//        header[8] = 'W'.code.toByte()
//        header[9] = 'A'.code.toByte()
//        header[10] = 'V'.code.toByte()
//        header[11] = 'E'.code.toByte()
//
//        // fmt sub-chunk
//        header[12] = 'f'.code.toByte() // Sub-chunk identifier
//        header[13] = 'm'.code.toByte()
//        header[14] = 't'.code.toByte()
//        header[15] = ' '.code.toByte() // Chunk size
//        header[16] = 16
//        header[17] = 0
//        header[18] = 0
//        header[19] = 0
//        // Audio format (PCM = 1)
//        header[20] = 1
//        header[21] = 0
//
//        // Number of channels (2 = stereo)
//        header[22] = (if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2).toByte()
//        header[23] = 0
//
//        // Sample rate
//        header[24] = (sampleRate and 0xff).toByte()
//        header[25] = (sampleRate shr 8 and 0xff).toByte()
//        header[26] = (sampleRate shr 16 and 0xff).toByte()
//        header[27] = (sampleRate shr 24 and 0xff).toByte()
//
//        // Byte rate (Sample rate * Number of channels * Bits per sample / 8)
//        val byteRate = sampleRate * (if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2) * if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 2 else 1
//        header[28] = (byteRate and 0xff).toByte()
//        header[29] = (byteRate shr 8 and 0xff).toByte()
//        header[30] = (byteRate shr 16 and 0xff).toByte()
//        header[31] = (byteRate shr 24 and 0xff).toByte()
//
//        // Block align (Number of channels * Bits per sample / 8)
//        header[32] = ((if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2) * if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 2 else 1).toByte()
//        header[33] = 0
//
//        // Bits per sample
//        header[34] = (if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 16 else 8).toByte()
//        header[35] = 0
//
//        // data sub-chunk
//        header[36] = 'd'.code.toByte() // Sub-chunk identifier
//        header[37] = 'a'.code.toByte()
//        header[38] = 't'.code.toByte()
//        header[39] = 'a'.code.toByte() // Chunk size
//        header[40] = (audioDataLength and 0xffL).toByte()
//        header[41] = (audioDataLength shr 8 and 0xffL).toByte()
//        header[42] = (audioDataLength shr 16 and 0xffL).toByte()
//        header[43] = (audioDataLength shr 24 and 0xffL).toByte()
//        outputStream.write(header)
//    }

//    public fun writeWavHeader(outputStream: OutputStream, totalAudioLen: Int, totalDataLen: Int, longSampleRate: Long, channels: Int, byteRate: Long) {
//        val header = ByteArray(44)
//
//        header[0] = 'R'.toByte() // RIFF/WAVE header
//        header[1] = 'I'.toByte()
//        header[2] = 'F'.toByte()
//        header[3] = 'F'.toByte()
//
//        header[4] = (totalDataLen and 0xFF).toByte() // file size (totalDataLen + 36)
//        header[5] = (totalDataLen shr 8 and 0xFF).toByte()
//        header[6] = (totalDataLen shr 16 and 0xFF).toByte()
//        header[7] = (totalDataLen shr 24 and 0xFF).toByte()
//
//        header[8] = 'W'.toByte() // WAVE format
//        header[9] = 'A'.toByte()
//        header[10] = 'V'.toByte()
//        header[11] = 'E'.toByte()
//
//        header[12] = 'f'.toByte() // 'fmt ' chunk
//        header[13] = 'm'.toByte()
//        header[14] = 't'.toByte()
//        header[15] = ' '.toByte()
//
//        header[16] = 16 // 16 for PCM
//        header[17] = 0
//        header[18] = 0
//        header[19] = 0
//
//        header[20] = 1 // 1 for PCM
//        header[21] = 0
//
//        header[22] = channels.toByte() // number of channels
//        header[23] = 0
//
//        header[24] = (longSampleRate and 0xFF).toByte() // sample rate
//        header[25] = (longSampleRate shr 8 and 0xFF).toByte()
//        header[26] = (longSampleRate shr 16 and 0xFF).toByte()
//        header[27] = (longSampleRate shr 24 and 0xFF).toByte()
//
//        header[28] = (byteRate and 0xFF).toByte() // byte rate
//        header[29] = (byteRate shr 8 and 0xFF).toByte()
//        header[30] = (byteRate shr 16 and 0xFF).toByte()
//        header[31] = (byteRate shr 24 and 0xFF).toByte()
//
//        header[32] = (channels * 8).toByte() // block align
//        header[33] = 0
//
//        header[34] = 8 // bits per sample
//        header[35] = 0
//
//        header[36] = 'd'.toByte() // 'data' chunk
//        header[37] = 'a'.toByte()
//        header[38] = 't'.toByte()
//        header[39] = 'a'.toByte()
//
//        header[40] = (totalAudioLen and 0xFF).toByte() // data size (totalDataLen)
//        header[41] = (totalAudioLen shr 8 and 0xFF).toByte()
//        header[42] = (totalAudioLen shr 16 and 0xFF).toByte()
//        header[43] = (totalAudioLen shr 24 and 0xFF).toByte()
//
//        outputStream.write(header, 0, 44)
//    }

//    @Throws(IOException::class)
//    fun writeWavHeader(outputStream: OutputStream, channelConfig: Int, sampleRate: Int, audioFormat: Int) {
//        val audioDataLength: Long = 0 // Set to 0 for now
//        val overallSize: Long = audioDataLength + 36 // Add header size
//        val header = ByteArray(44)
//
//        // RIFF chunk descriptor
//        header[0] = 'R'.code.toByte()
//        header[1] = 'I'.code.toByte()
//        header[2] = 'F'.code.toByte()
//        header[3] = 'F'.code.toByte()
//
//        // Overall size (file size - 8 bytes for RIFF and WAVE tags)
//        header[4] = (overallSize and 0xffL).toByte()
//        header[5] = (overallSize shr 8 and 0xffL).toByte()
//        header[6] = (overallSize shr 16 and 0xffL).toByte()
//        header[7] = (overallSize shr 24 and 0xffL).toByte()
//
//        // WAVE chunk
//        header[8] = 'W'.code.toByte()
//        header[9] = 'A'.code.toByte()
//        header[10] = 'V'.code.toByte()
//        header[11] = 'E'.code.toByte()
//
//        // fmt sub-chunk
//        header[12] = 'f'.code.toByte() // Sub-chunk identifier
//        header[13] = 'm'.code.toByte()
//        header[14] = 't'.code.toByte()
//        header[15] = ' '.code.toByte() // Chunk size
//        header[16] = 16
//        header[17] = 0
//        header[18] = 0
//        header[19] = 0
//        // Audio format (PCM = 1)
//        header[20] = 1
//        header[21] = 0
//
//        // Number of channels (2 = stereo)
//        header[22] = (if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2).toByte()
//        header[23] = 0
//
//        // Sample rate
//        header[24] = (sampleRate and 0xff).toByte()
//        header[25] = (sampleRate shr 8 and 0xff).toByte()
//        header[26] = (sampleRate shr 16 and 0xff).toByte()
//        header[27] = (sampleRate shr 24 and 0xff).toByte()
//
//        // Byte rate (Sample rate * Number of channels * Bits per sample / 8)
//        val byteRate = sampleRate * (if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2) * if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 2 else 1
//        header[28] = (byteRate and 0xff).toByte()
//        header[29] = (byteRate shr 8 and 0xff).toByte()
//        header[30] = (byteRate shr 16 and 0xff).toByte()
//        header[31] = (byteRate shr 24 and 0xff).toByte()
//
//        // Block align (Number of channels * Bits per sample / 8)
//        header[32] = ((if (channelConfig == AudioFormat.CHANNEL_IN_MONO) 1 else 2) * if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 2 else 1).toByte()
//        header[33] = 0
//
//        // Bits per sample
//        header[34] = (if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) 16 else 8).toByte()
//        header[35] = 0
//
//        // data sub-chunk
//        header[36] = 'd'.code.toByte() // Sub-chunk identifier
//        header[37] = 'a'.code.toByte()
//        header[38] = 't'.code.toByte()
//        header[39] = 'a'.code.toByte() // Chunk size
//        header[40] = (audioDataLength and 0xffL).toByte()
//        header[41] = (audioDataLength shr 8 and 0xffL).toByte()
//        header[42] = (audioDataLength shr 16 and 0xffL).toByte()
//        header[43] = (audioDataLength shr 24 and 0xffL).toByte()
//
//        outputStream.write(header)
//    }
}