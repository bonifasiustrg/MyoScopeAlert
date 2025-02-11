package com.apicta.myoscopealert.bt

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.apicta.myoscopealert.wav.Wav
import java.io.*
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.text.format

//@SuppressLint("MissingPermission")
//class ThreadConnected(
//    socket: BluetoothSocket,
//    switch: Boolean,
//    private val context: Context,
//    private val title: String,
////    private val updateChart: () -> Unit
//) : Thread() {
//
//    private val mmSocket = socket
//    private val mmInStream: InputStream = socket.inputStream
//    private val mmBuffer: ByteArray = ByteArray(BUFFER_SIZE)
//    private val byteArrayOutputStream = ByteArrayOutputStream()
//    private lateinit var data: ByteArray
//
//    private var isOn = switch
//    private var startTime: Long = 0
//    @RequiresApi(Build.VERSION_CODES.S)
//    override fun run() {
//        var numBytes: Int = 0
//        var accumulatedData = StringBuilder() // Buffer untuk menyimpan data selama rekaman
//
//        byteArrayOutputStream.reset()
//        while (isOn){
//            try {
//                numBytes = mmInStream.read(mmBuffer)
//                // Gabungkan data ke buffer, tetapi jangan disimpan ke file dulu
//                val fileContent = mmBuffer.take(numBytes).joinToString(", ") { it.toString() }
//                accumulatedData.append("$fileContent\n")
//                Log.d("Bluetooth Debug", "Raw Data: ${mmBuffer.take(numBytes).joinToString(", ") { it.toInt().toString() }}")
//                Log.d("Bluetooth Debug", "Unsigned Data: ${mmBuffer.take(numBytes).map { (it.toInt() and 0xFF) }}")
//
//
//
//                byteArrayOutputStream.write(mmBuffer, 0, numBytes)
//
//            } catch (e: IOException){
//                Log.e("newBT Bluetooth record", "Input stream was disconnected", e)
//                break
//            }
//            Log.e("newBT Bluetooth record", "numBytes: $numBytes")
//            val currentTime = System.currentTimeMillis()
//            if (startTime == 0L && numBytes > 0) {
//                startTime = System.currentTimeMillis()
//            }
//            val elapsedTime = currentTime - startTime
//            val elapsedTimeFloat = millisToSeconds(elapsedTime)
//
//            if (elapsedTimeFloat >= 10.0){
//                startTime = currentTime
//                Log.e("newBT " + TAG, "pcgArray: ${ArrayReceiver.pcgArray}")
//                Log.e("newBT " + TAG, "nilai maksimum: ${ArrayReceiver.pcgArray.maxOrNull()}")
//                Log.e("newBT " + TAG, "timeArray: ${ArrayReceiver.timeArray}")
//                isOn = false
//
//                val contentValues = ContentValues()
////                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$title-${System.currentTimeMillis()}.wav")
//                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, title)
//                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav")
//
//                val contentResolver = context?.contentResolver
//                val uri = contentResolver?.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
//                Log.e("newBT save", "$uri")
//                Log.e("newBT save", "${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}")
//                val desiredByteCount = ArrayReceiver.timeArray.last().toInt() * SAMPLE_RATE
//                Log.e("newBT " + TAG, "desiredByCount: $desiredByteCount")
//                Log.e("newBT " + TAG, "lastTime: ${ArrayReceiver.timeArray.last()}")
//
//                data = byteArrayOutputStream.toByteArray()
//
//
//                val normalizedData = normalizeAudio(data)
//
//                try {
//                    val outputStream: OutputStream = contentResolver?.openOutputStream(uri!!)!!
//                    val wavHeader = Wav.generateWavHeader(
//                        normalizedData,
//                        CHANNEL_CONFIG,
//                        SAMPLE_RATE,
//                        AUDIO_FORMAT
//                    )
//
//                    // Tuliskan header .wav terlebih dahulu
//                    outputStream.write(wavHeader, 0, wavHeader.size)
//                    outputStream.write(normalizedData)
//                    // Tuliskan data audio dalam buffer kecil untuk efisiensi
//
//                    val buffer = ByteArray(BUFFER_SIZE)
//                    Log.e("buffer", BUFFER_SIZE.toString())
//                    var offset = 0
//                    while (offset < data.size) {
//                        val length = minOf(buffer.size, data.size - offset)
//                        System.arraycopy(data, offset, buffer, 0, length)
//                        outputStream.write(buffer, 0, length)
//                        offset += length
//                    }
//                    outputStream.close()
//                    Log.e("File Saved", "Saved WAV file to: $uri")
//                } catch (e: IOException){
//                    e.printStackTrace()
//                }
//            }
//
//
////            val receivedInt = ByteBuffer.wrap(mmBuffer, 0, numBytes).order(ByteOrder.LITTLE_ENDIAN).int
//            val unsignedData = mmBuffer.take(numBytes).map { it.toInt() and 0xFF }
//            val receivedInt = unsignedData.firstOrNull() ?: 0
//            Log.e("newBT Bluetooth record", "received: ${receivedInt}")
//            ArrayReceiver.pcgArray.add(receivedInt)
//            ArrayReceiver.timeArray.add(elapsedTimeFloat)
//        }
//
//        // Setelah loop selesai, simpan semua data dari buffer ke file
//        try {
//            val fileName = "bluetooth_data_${generateRandomIntFromTime()}.txt"
//            val file = File(context.getExternalFilesDir(null), fileName)
//            file.writeText(accumulatedData.toString())
//            Log.d("Bluetooth Debug", "Final data written to file: ${file.absolutePath}")
//        } catch (e: IOException) {
//            Log.e("Bluetooth Debug", "Error saving final file", e)
//        }
//    }
//    fun cancel(){
//        try {
//            mmSocket.close()
//
//            // Reset data saat socket ditutup
//            byteArrayOutputStream.reset()
//            ArrayReceiver.pcgArray.clear()
//            ArrayReceiver.timeArray.clear()
//            Log.d("ThreadStatus", "Thread dihentikan, data direset")
//        } catch (e: IOException){
//            Log.e("Bluetooth record", "Could not close the connect socket", e)
//        }
//    }
//    fun millisToSeconds(millis: Long): Float {
//        return (millis / 1000.0).toFloat().let { "%.3f".format(it).toFloat() }
//    }
//
//    companion object{
//        private const val TAG = "Record Patient Screen vm"
//        private const val REQUEST_PERMISSION = 1
//        const val SAMPLE_RATE = 8000
//        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
//        // encoding float ok but time not ok
//        //
////        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_FLOAT
//        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
////        val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
////        val BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
//        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
//        const val AMPLIFICATION_FACTOR = 3
////        private const val BUFFER_SIZE = 1024
//
//
//
//    }
//
//}

@SuppressLint("MissingPermission")
class ThreadConnected(
    private val socket: BluetoothSocket,
    private var isOn: Boolean,
    private val context: Context,
    private val title: String,
    private val bluetoothViewModel: BluetoothViewModel
) : Thread() {

    private val mmSocket: BluetoothSocket = socket
    private val mmInStream: InputStream = socket.inputStream
    private val mmOutStream: OutputStream = socket.outputStream
    private val mmBuffer: ByteArray = ByteArray(BUFFER_SIZE)
    private val byteArrayOutputStream = ByteArrayOutputStream()
    private lateinit var data: ByteArray
    private var stopCommandSent = false

    private var startTime: Long = 0
    private var rawOutputStream: FileOutputStream? = null
    private val receivedIntegers = mutableListOf<Int>()
    @RequiresApi(Build.VERSION_CODES.S)
    override fun run() {
        try {
            if (mmSocket.isConnected) {
                bluetoothViewModel.setBluetoothName(mmSocket.remoteDevice.name) // Update status di ViewModel
                mmOutStream.write("S".toByteArray(Charsets.UTF_8))
                mmOutStream.flush()
                Log.d(TAG, "Sent start command 'S'")

            } else {
                Log.e(TAG, "Socket sudah tertutup, tidak bisa mengirim perintah start.")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error sending start command", e)
        }

        var numBytes: Int
        val accumulatedData = StringBuilder()
        byteArrayOutputStream.reset()


        val docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val rawFile = File(docsDir, "$title.bin")

        try {
            rawOutputStream = FileOutputStream(rawFile)
        } catch (e: IOException) {
            Log.e(TAG, "Error creating raw file", e)
        }
        var fileContent: String = ""
        var unsignedData: List<Int> = emptyList()
        while (isOn && mmSocket.isConnected) {
            try {
                if (!mmSocket.isConnected) {
                    Log.e("ThreadConnected", "Socket is not connected, stopping thread")
                    break
                }

                numBytes = mmInStream.read(mmBuffer)
                if (numBytes > 0) {
                    // Simpan data mentah ke buffer
                    byteArrayOutputStream.write(mmBuffer, 0, numBytes)

                    // **Simpan data mentah ke file .bin**
                    rawOutputStream?.write(mmBuffer, 0, numBytes)

                    // Konversi ke integer (unsigned 8-bit)
                    unsignedData = mmBuffer.take(numBytes).map { it.toInt() and 0xFF }
                    receivedIntegers.addAll(unsignedData)

                    // Logging dan akumulasi data untuk penyimpanan .txt
                    fileContent = unsignedData.joinToString(", ")
                    accumulatedData.append("$fileContent\n")
                    Log.e("ThreadConnected", "Received: ${unsignedData.firstOrNull() ?: 0}")



                    val currentTime = System.currentTimeMillis()
                    if (startTime == 0L) {
                        startTime = currentTime
                    }
                    val elapsedTime = millisToSeconds(currentTime - startTime)

                    if (elapsedTime >= 10.0 && !stopCommandSent) {
                        sendStopCommand()
                        isOn = false
                    }
                } else if (numBytes == -1) { // <-- Handle EOF atau disconnect
                    Log.e("ThreadConnected", "Socket disconnected, Input stream reached EOF, stopping thread")
                    break
                }
            } catch (e: IOException) {
                Log.e("ThreadConnected", "Input stream was disconnected", e)
                break
            }
        }
        Log.d("Bluetooth Debug", "Raw Data: $fileContent")
        Log.d("Bluetooth Debug", "Unsigned Data: $unsignedData")
        // **Tutup file .bin setelah rekaman selesai**
        rawOutputStream?.close()

        // **Simpan data integer ke file .txt di Documents**
        saveIntegerDataToTxt(receivedIntegers)

        // **Konversi ke WAV & simpan**
        data = byteArrayOutputStream.toByteArray()
        saveAsWav(data)
    }

    private fun saveAsWav(data: ByteArray) {
        val normalizedData = normalizeAudio(data)

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, title)
                put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav")
            }
            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    val wavHeader = Wav.generateWavHeader(normalizedData, CHANNEL_CONFIG, SAMPLE_RATE, AUDIO_FORMAT)
                    outputStream.write(wavHeader, 0, wavHeader.size)
                    outputStream.write(normalizedData)
                    Log.e("File Saved", "Saved WAV file to: $uri")
                }
            }
        } catch (e: IOException) {
            Log.e("ThreadConnected", "Error saving WAV file", e)
        }
    }

    private fun saveIntegerDataToTxt(data: List<Int>) {
        try {
            val docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val txtFile = File(docsDir, "$title.txt")
            txtFile.writeText(data.joinToString(", "))
            Log.d("ThreadConnected", "Integer data written to: ${txtFile.absolutePath}")
        } catch (e: IOException) {
            Log.e("ThreadConnected", "Error saving integer data to .txt", e)
        }
    }

    fun sendStopCommand() {
        if (!stopCommandSent) { // Hanya kirim jika belum dikirim
            try {
                mmOutStream.write("T".toByteArray(Charsets.UTF_8))
                mmOutStream.flush()
                Log.d(TAG, "Sent stop command 'T'")
                stopCommandSent = true // Tandai bahwa stop command sudah terkirim
            } catch (e: IOException) {
                Log.e(TAG, "Error sending stop command", e)
            }
        }
    }

    fun cancel() {
        if (isOn) {
            sendStopCommand()
        }
        try {
            isOn = false
            mmSocket.close()
            byteArrayOutputStream.reset()
            Log.d("ThreadConnected", "Thread dihentikan, data direset")
        } catch (e: IOException) {
            Log.e("ThreadConnected", "Could not close the connect socket", e)
        }
    }

    fun millisToSeconds(millis: Long): Float {
        return (millis / 1000.0).toFloat().let { "%.3f".format(it).toFloat() }
    }

    companion object {
        private const val TAG = "ThreadConnected"
        const val SAMPLE_RATE = 8000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
    }
}



@RequiresApi(Build.VERSION_CODES.O)
fun generateRandomIntFromTime(): Int {
    val currentTimeMillis = LocalDateTime.now().toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
    val random = Random(currentTimeMillis)
    return random.nextInt(100000, 999999) // Generate a 6-digit random integer
}
fun amplifyAudio(input: ByteArray, amplificationFactor: Float): ByteArray {
    val amplifiedData = ByteArray(input.size)
    for (i in input.indices step 2) {
        val sample = ((input[i + 1].toInt() shl 8) or (input[i].toInt() and 0xFF)).toShort()
        val amplifiedSample = (sample * amplificationFactor).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        amplifiedData[i] = (amplifiedSample.toInt() and 0xFF).toByte()
        amplifiedData[i + 1] = ((amplifiedSample.toInt() shr 8) and 0xFF).toByte()
    }
    return amplifiedData
}

fun normalizeAudio(input: ByteArray): ByteArray {
    val normalizedData = ByteArray(input.size)

    // Konversi ke array of samples
    val samples = ShortArray(input.size / 2)
    for (i in samples.indices) {
        samples[i] = ((input[i * 2 + 1].toInt() shl 8) or (input[i * 2].toInt() and 0xFF)).toShort()
    }

    // Step 1: Remove DC offset (center pada 0)
    val dcOffset = samples.average()
    for (i in samples.indices) {
        samples[i] = (samples[i] - dcOffset).toInt().toShort()
    }

    // Step 2: Cari nilai amplitudo maksimum
    var maxAmplitude = 0.0
    for (sample in samples) {
        maxAmplitude = maxOf(maxAmplitude, Math.abs(sample.toDouble()))
    }

    // Step 3: Hitung faktor normalisasi untuk -1.0 dB
    // -1.0 dB = 10^(-1.0/20) ≈ 0.8913
    val targetAmplitude = (Short.MAX_VALUE * 0.8913).toInt()

    if (maxAmplitude == 0.0) return input // Hindari pembagian dengan nol
    val normalizationFactor = targetAmplitude / maxAmplitude

    // Step 4: Terapkan normalisasi
    for (i in samples.indices) {
        val normalizedSample = (samples[i] * normalizationFactor).toInt()
            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            .toShort()

        // Konversi kembali ke bytes
        normalizedData[i * 2] = (normalizedSample.toInt() and 0xFF).toByte()
        normalizedData[i * 2 + 1] = ((normalizedSample.toInt() shr 8) and 0xFF).toByte()
    }

    return normalizedData
}
