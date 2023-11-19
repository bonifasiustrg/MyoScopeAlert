package com.apicta.myoscopealert.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.apicta.myoscopealert.models.FileModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*FUNCTION*/
fun getCurrentTimeNoClock(): String {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    val currentTime = Date()
    return dateFormat.format(currentTime)
}
fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss", Locale.getDefault())
    val currentTime = Date()
    return dateFormat.format(currentTime)
}

fun convertIntArrayToWav(intArray: IntArray, outputFile: File) {
    val sampleRate = 44100 // Sample rate in Hz
//        val sampleRate = 8000 // Sample rate in Hz
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    val channelConfig = AudioFormat.CHANNEL_IN_MONO
    val bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, audioFormat)


    val bitsPerSample = 16 // 16-bit audio
    val numChannels = 1 // Mono audio
    val audioTrack = AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM)

    val outputStream = FileOutputStream(outputFile)
    val header = generateWavHeader(intArray.size * 2, sampleRate, numChannels, bitsPerSample)
    outputStream.write(header)

    audioTrack.play()

    for (value in intArray) {
        val byteValue = value.toByte()
        audioTrack.write(byteArrayOf(byteValue, byteValue), 0, 2)
        outputStream.write(byteArrayOf(byteValue, byteValue))
    }

    audioTrack.stop()
    audioTrack.release()
    outputStream.close()
}

fun generateWavHeader(dataSize: Int, sampleRate: Int, numChannels: Int, bitsPerSample: Int): ByteArray {
    val headerSize = 44
    val totalSize = dataSize + headerSize - 8
    val header = ByteArray(headerSize)

//        val audioDataLength = outputStream.channel.size() - 44 // Subtract header size
//        val overallSize = audioDataLength + 36 // Add header size

    // ChunkID
    header[0] = 'R'.code.toByte()
    header[1] = 'I'.code.toByte()
    header[2] = 'F'.code.toByte()
    header[3] = 'F'.code.toByte()

    // ChunkSize
    header[4] = (totalSize and 0xff).toByte()
    header[5] = (totalSize shr 8 and 0xff).toByte()
    header[6] = (totalSize shr 16 and 0xff).toByte()
    header[7] = (totalSize shr 24 and 0xff).toByte()

    // Format
    header[8] = 'W'.code.toByte()
    header[9] = 'A'.code.toByte()
    header[10] = 'V'.code.toByte()
    header[11] = 'E'.code.toByte()

    // Subchunk1ID
    header[12] = 'f'.code.toByte()
    header[13] = 'm'.code.toByte()
    header[14] = 't'.code.toByte()
    header[15] = ' '.code.toByte()

    // Subchunk1Size
    header[16] = 16 // PCM format
    header[17] = 0
    header[18] = 0
    header[19] = 0

    // AudioFormat
    header[20] = 1 // PCM format
    header[21] = 0

    // NumChannels
    // Number of channels (2 = stereo)`
    header[22] = numChannels.toByte()
//        header[22] = (if (numChannels == AudioFormat.CHANNEL_IN_MONO) 1 else 2).toByte()
    header[23] = 0


    // SampleRate
    header[24] = (sampleRate and 0xff).toByte()
    header[25] = (sampleRate shr 8 and 0xff).toByte()
    header[26] = (sampleRate shr 16 and 0xff).toByte()
    header[27] = (sampleRate shr 24 and 0xff).toByte()

    // Byte rate (Sample rate * Number of channels * Bits per sample / 8)
    val byteRate = sampleRate * numChannels * bitsPerSample / 8
//        val byteRate = sampleRate * (if (numChannels == AudioFormat.CHANNEL_IN_MONO) 1 else 2) * if (numChannels == AudioFormat.ENCODING_PCM_16BIT) 2 else 1
    header[28] = (byteRate and 0xff).toByte()
    header[29] = (byteRate shr 8 and 0xff).toByte()
    header[30] = (byteRate shr 16 and 0xff).toByte()
    header[31] = (byteRate shr 24 and 0xff).toByte()

    // Block align (Number of channels * Bits per sample / 8)
    val blockAlign = numChannels * bitsPerSample / 8
//        header[32] = ((if (CHANNEL_CONFIG == AudioFormat.CHANNEL_IN_MONO) 1 else 2) * if (AUDIO_FORMAT == AudioFormat.ENCODING_PCM_16BIT) 2 else 1).toByte()
    header[32] = (blockAlign and 0xff).toByte()
    header[33] = (blockAlign shr 8 and 0xff).toByte()
//        header[33] = 0


    // BitsPerSample
    header[34] = bitsPerSample.toByte()
//        header[34] = (if (AUDIO_FORMAT == AudioFormat.ENCODING_PCM_16BIT) 16 else 8).toByte()
    header[35] = 0

    // Subchunk2ID
    header[36] = 'd'.code.toByte()
    header[37] = 'a'.code.toByte()
    header[38] = 't'.code.toByte()
    header[39] = 'a'.code.toByte()

    // Subchunk2Size
    header[40] = (dataSize and 0xff).toByte()
    header[41] = (dataSize shr 8 and 0xff).toByte()
    header[42] = (dataSize shr 16 and 0xff).toByte()
    header[43] = (dataSize shr 24 and 0xff).toByte()

    return header
}

@RequiresApi(Build.VERSION_CODES.S)
fun checkBtPermission(context: Context): Boolean {
    if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            1
        )
    }
    val check = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
    return check == PackageManager.PERMISSION_GRANTED
}

fun fileListDir(context:Context, fileList: ArrayList<FileModel>) {
    val resolver = context.contentResolver
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATE_MODIFIED)
    val selection = MediaStore.Audio.Media.DATA + " like ? "

    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    val selectionArgs = arrayOf("%${musicDir.absolutePath}%")
//    val selectionArgs = arrayOf("%/storage/emulated/0/Music/%")

    val sortOrder = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"

    val cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder)

    cursor?.use {
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

        while (cursor.moveToNext()) {
            val name = cursor.getString(nameColumn)
            val date = cursor.getLong(dateColumn)

            if (name.endsWith(".wav")) fileList.add(FileModel(name, date))
        }
    }

    Log.e("newBT save", "$uri")

    Log.e("newBT filelist", "$musicDir")
    Log.e("newBT filelist2", "$selectionArgs")
}