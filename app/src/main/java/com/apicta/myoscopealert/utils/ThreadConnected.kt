package com.apicta.myoscopealert.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.apicta.myoscopealert.utils.Wav.adjustVolumeWithCompression
import com.apicta.myoscopealert.wav.AudioLowPassFilter
//import be.tarsos.dsp.AudioEvent
//import be.tarsos.dsp.AudioProcessor
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.random.Random
import kotlin.text.format
import kotlin.text.map

@SuppressLint("MissingPermission")
class ThreadConnected(
    socket: BluetoothSocket,
    switch: Boolean,
    private val context: Context,
    private val title: String,
//    private val updateChart: () -> Unit
) : Thread() {

    private val mmSocket = socket
    private val mmInStream: InputStream = socket.inputStream
    private val mmBuffer: ByteArray = ByteArray(BUFFER_SIZE)
    private val byteArrayOutputStream = ByteArrayOutputStream()
    private lateinit var data: ByteArray

    private var isOn = switch
    private var startTime: Long = 0
    @RequiresApi(Build.VERSION_CODES.S)
    override fun run() {
        var numBytes: Int = 0
        var accumulatedData = StringBuilder() // Buffer untuk menyimpan data selama rekaman

        byteArrayOutputStream.reset()
        while (isOn){
            try {
                numBytes = mmInStream.read(mmBuffer)
                // Amplifikasi data
                for (i in 0 until numBytes step 2) {
                    val value = ByteBuffer.wrap(mmBuffer, i, 2).order(ByteOrder.LITTLE_ENDIAN).short
                    val amplifiedValue = (value * AMPLIFICATION_FACTOR).coerceIn(Short.MIN_VALUE.toInt()
                        .toInt(), Short.MAX_VALUE.toInt()
                    ).toInt()
                    mmBuffer[i] = (amplifiedValue and 0xFF).toByte()
                    mmBuffer[i + 1] = ((amplifiedValue shr 8) and 0xFF).toByte()
                }
//                Log.d("Bluetooth Debug", "Raw data received: ${mmBuffer.take(numBytes).joinToString(", ") { it.toString() }}")

//                // Simpan data ke file internal
//                val fileName = "bluetooth_data_${generateRandomIntFromTime()}.txt"
//                val fileContent = mmBuffer.take(numBytes).joinToString(", ") { it.toString() }
//                val file = File(context.getExternalFilesDir(null), fileName)
//                file.appendText("$fileContent\n")
//                Log.d("Bluetooth Debug", "Data written to file: ${file.absolutePath}")
                // Gabungkan data ke buffer, tetapi jangan disimpan ke file dulu
                val fileContent = mmBuffer.take(numBytes).joinToString(", ") { it.toString() }
                accumulatedData.append("$fileContent\n")
                Log.d("Bluetooth Debug", "Raw Data: ${mmBuffer.take(numBytes).joinToString(", ") { it.toInt().toString() }}")
                Log.d("Bluetooth Debug", "Unsigned Data: ${mmBuffer.take(numBytes).map { (it.toInt() and 0xFF) }}")



                byteArrayOutputStream.write(mmBuffer, 0, numBytes)

            } catch (e: IOException){
                Log.e("newBT Bluetooth record", "Input stream was disconnected", e)
                break
            }
            Log.e("newBT Bluetooth record", "numBytes: $numBytes")
            val currentTime = System.currentTimeMillis()
            if (startTime == 0L && numBytes > 0) {
                startTime = System.currentTimeMillis()
            }
            val elapsedTime = currentTime - startTime
            val elapsedTimeFloat = millisToSeconds(elapsedTime)

            if (elapsedTimeFloat >= 10.0){
                startTime = currentTime
                Log.e("newBT " + TAG, "pcgArray: ${ArrayReceiver.pcgArray}")
                Log.e("newBT " + TAG, "nilai maksimum: ${ArrayReceiver.pcgArray.maxOrNull()}")
                Log.e("newBT " + TAG, "timeArray: ${ArrayReceiver.timeArray}")
                isOn = false

                val contentValues = ContentValues()
//                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$title-${System.currentTimeMillis()}.wav")
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, title)
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav")

                val contentResolver = context?.contentResolver
                val uri = contentResolver?.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
                Log.e("newBT save", "$uri")
                Log.e("newBT save", "${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}")
                val desiredByteCount = ArrayReceiver.timeArray.last().toInt() * SAMPLE_RATE
                Log.e("newBT " + TAG, "desiredByCount: $desiredByteCount")
                Log.e("newBT " + TAG, "lastTime: ${ArrayReceiver.timeArray.last()}")

                data = byteArrayOutputStream.toByteArray()
//                val wavData: ByteArray = Wav.generateWavHeader(data, CHANNEL_CONFIG, SAMPLE_RATE, AUDIO_FORMAT)
//                val amplificationFactor = 2.0f // Ubah sesuai kebutuhan (contoh: 2x amplifikasi)
//                val amplifiedData = amplifyAudio(data, amplificationFactor)


                val normalizedData = normalizeAudio(data)

                try {
                    val outputStream: OutputStream = contentResolver?.openOutputStream(uri!!)!!
                    val wavHeader = Wav.generateWavHeader(normalizedData, CHANNEL_CONFIG, SAMPLE_RATE, AUDIO_FORMAT)

                    // Tuliskan header .wav terlebih dahulu
                    outputStream.write(wavHeader, 0, wavHeader.size)
                    outputStream.write(normalizedData)
                    // Tuliskan data audio dalam buffer kecil untuk efisiensi

                    val buffer = ByteArray(BUFFER_SIZE)
                    Log.e("buffer", BUFFER_SIZE.toString())
                    var offset = 0
                    while (offset < data.size) {
                        val length = minOf(buffer.size, data.size - offset)
                        System.arraycopy(data, offset, buffer, 0, length)
                        outputStream.write(buffer, 0, length)
                        offset += length
                    }
                    outputStream.close()
                    Log.e("File Saved", "Saved WAV file to: $uri")
//                    val outputStream: OutputStream = contentResolver?.openOutputStream(uri!!)!!
////                    val outputStream: FileOutputStream = FileOutputStream(file)
//                    if (desiredByteCount <= wavData.size){
////                                outputStream.write(wavData)
//                        outputStream.write(wavData, 0, desiredByteCount)
//                        Log.e("newBT " + TAG, "run: execute A")
//                    } else {
//                        outputStream.write(wavData)
//                        Log.e("newBT " + TAG, "run: execute B")
//                    }
//                    outputStream.close()
//                    Log.e("newBT save" + TAG, "File saved to: $uri")
                } catch (e: IOException){
                    e.printStackTrace()
                }
            }


//            val receivedInt = ByteBuffer.wrap(mmBuffer, 0, numBytes).order(ByteOrder.LITTLE_ENDIAN).int
            val unsignedData = mmBuffer.take(numBytes).map { it.toInt() and 0xFF }
            val receivedInt = unsignedData.firstOrNull() ?: 0
            Log.e("newBT Bluetooth record", "received: ${receivedInt}")
            ArrayReceiver.pcgArray.add(receivedInt)
            ArrayReceiver.timeArray.add(elapsedTimeFloat)

//                    updateChart()
        }
        // Setelah loop selesai, simpan semua data dari buffer ke file
        try {
            val fileName = "bluetooth_data_${generateRandomIntFromTime()}.txt"
            val file = File(context.getExternalFilesDir(null), fileName)
            file.writeText(accumulatedData.toString())
            Log.d("Bluetooth Debug", "Final data written to file: ${file.absolutePath}")
        } catch (e: IOException) {
            Log.e("Bluetooth Debug", "Error saving final file", e)
        }
    }
    fun cancel(){
        try {
            mmSocket.close()
        } catch (e: IOException){
            Log.e("Bluetooth record", "Could not close the connect socket", e)
        }
    }
    fun millisToSeconds(millis: Long): Float {
        return (millis / 1000.0).toFloat().let { "%.3f".format(it).toFloat() }
    }

    companion object{
        private const val TAG = "Record Patient Screen vm"
        private const val REQUEST_PERMISSION = 1
        const val SAMPLE_RATE = 8000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        // encoding float ok but time not ok
        //
//        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_FLOAT
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
//        val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        val BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)

        const val AMPLIFICATION_FACTOR = 3
//        private const val BUFFER_SIZE = 1024
    }

}

//@SuppressLint("MissingPermission")
//class ThreadConnected(
//    socket: BluetoothSocket,
//    switch: Boolean,
//    private val context: Context,
//    private val title: String
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
//
//    // Define filter parameters
//    private val filterOrder = 2
//    private val cutoffFreq = 3.0 // Adjust cutoff frequency as needed
//    private val sampleRate = SAMPLE_RATE
//    private val firCoefficients = createLowPassFIR(filterOrder, cutoffFreq, sampleRate)
//    private val firFilterProcessor = FIRFilterProcessor(firCoefficients)
//
//    @RequiresApi(Build.VERSION_CODES.S)
//    override fun run() {
//        var numBytes: Int
//
//        while (isOn) {
//            try {
//                numBytes = mmInStream.read(mmBuffer)
//                byteArrayOutputStream.write(mmBuffer, 0, numBytes)
//            } catch (e: IOException) {
//                Log.e("newBT Bluetooth record", "Input stream was disconnected", e)
//                break
//            }
//
//            val currentTime = System.currentTimeMillis()
//            if (startTime == 0L) startTime = currentTime
//            val elapsedTimeFloat = millisToSeconds(currentTime - startTime)
//
//            if (elapsedTimeFloat >= 12) {
//                saveAudioFile()
//                isOn = false
//            }
//
//            // Process audio data through the FIR filter
//            val audioData = ByteBuffer.wrap(mmBuffer, 0, numBytes).order(ByteOrder.LITTLE_ENDIAN).float
////            firFilterProcessor.processAudio(audioData)
//            ArrayReceiver.pcgArray.add(audioData)
//            ArrayReceiver.timeArray.add(elapsedTimeFloat)
//        }
//    }
//
//    private fun saveAudioFile() {
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, title)
//            put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav")
//        }
//
//        val contentResolver = context.contentResolver
//        val uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
//        data = byteArrayOutputStream.toByteArray()
//        val wavData = Wav.generateWavHeader(data, CHANNEL_CONFIG, SAMPLE_RATE, AUDIO_FORMAT)
//
//        uri?.let {
//            contentResolver.openOutputStream(uri)?.use { outputStream ->
//                outputStream.write(wavData)
//            }
//        }
//    }
//
//    fun cancel() {
//        try {
//            mmSocket.close()
//        } catch (e: IOException) {
//            Log.e("Bluetooth record", "Could not close the connect socket", e)
//        }
//    }
//
//    fun millisToSeconds(millis: Long): Float {
//        return (millis / 1000.0).toFloat().let { "%.3f".format(it).toFloat() }
//    }
//
//    companion object {
//        private const val TAG = "Thread connected"
//        const val SAMPLE_RATE = 24000
//        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
//        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_FLOAT
//        val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
//    }
//
////    private fun createLowPassFIR(order: Int, cutoffFreq: Double, sampleRate: Int): DoubleArray {
////        val coefficients = DoubleArray(order + 1)
////        val fc = cutoffFreq / sampleRate
////        for (i in 0..order) {
////            coefficients[i] = if (i == order / 2) {
////                2 * Math.PI * fc
////            } else {
////                Math.sin(2 * Math.PI * fc * (i - order / 2)) / (i - order / 2) *
////                        (0.54 - 0.46 * Math.cos(2 * Math.PI * i / order))
////            }
////        }
////        return coefficients
////    }
////
////    inner class FIRFilterProcessor(private val coefficients: DoubleArray) : AudioProcessor {
////        private val buffer = DoubleArray(coefficients.size)
////        private var bufferIndex = 0
////
////        fun processAudio(audioSample: Float): Float {
////            buffer[bufferIndex] = audioSample.toDouble()
////            var output = 0.0
////
////            var index = bufferIndex
////            for (j in coefficients.indices) {
////                output += coefficients[j] * buffer[index]
////                index = if (index == 0) buffer.size - 1 else index - 1
////            }
////
////            bufferIndex = (bufferIndex + 1) % buffer.size
////            return output.toFloat()
////        }
////
////        override fun process(audioEvent: AudioEvent?): Boolean = true
////        override fun processingFinished() {}
////    }
//}


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
//
//        while (isOn){
//            try {
//                numBytes = mmInStream.read(mmBuffer)
//                byteArrayOutputStream.write(mmBuffer, 0, numBytes)
//
//            } catch (e: IOException){
//                Log.e("newBT Bluetooth record", "Input stream was disconnected", e)
//                break
//            }
//            Log.e("newBT Bluetooth record", "numBytes: $numBytes")
//            val currentTime = System.currentTimeMillis()
//            if (startTime == 0L){
//                startTime = currentTime
//            }
//            val elapsedTime = currentTime - startTime
//            val elapsedTimeFloat = millisToSeconds(elapsedTime)
//
//            if (elapsedTimeFloat >= 12/*30.0*/){
//                startTime = currentTime
//                Log.e("newBT " + TAG, "pcgArray: ${ArrayReceiver.pcgArray}")
//                Log.e("newBT " + TAG, "nilai maksimum: ${ArrayReceiver.pcgArray.maxOrNull()}")
////                Log.e("newBT " + TAG, "timeArray: ${ArrayReceiver.timeArray}")
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
//                Log.e("newBT " + TAG, "lastTime: ${ArrayReceiver.timeArray.last().toInt()}")
//
////                val contextWrapper = ContextWrapper(context)
////                val externalStorage: File = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)!!
////
////                val audioDirPath = externalStorage.absolutePath
////                val fileName = "${System.currentTimeMillis()}-$title.wav"
////                val filePath = "$audioDirPath/$fileName"
////
////                val file = File(filePath)
////                file.createNewFile()
//
//                data = byteArrayOutputStream.toByteArray()
//                val gainFactor = 3.0f // Coba nilai yang lebih tinggi
////                val slowdownFactor = 1f // Nilai > 1 akan memperlambat audio
//                val adjustedAudioData = adjustVolumeWithCompression(data, gainFactor)
////                val slowedAudioData = slowDownAudio(adjustedAudioData, slowdownFactor)
////                val adjustedSampleRate = (SAMPLE_RATE / slowdownFactor).toInt()
//
////                val wavData: ByteArray = Wav.generateWavHeader(slowedAudioData, CHANNEL_CONFIG, SAMPLE_RATE, AUDIO_FORMAT)
//                val wavData: ByteArray = Wav.generateWavHeader(adjustedAudioData, CHANNEL_CONFIG, SAMPLE_RATE, AUDIO_FORMAT)
//
//                val filteredWavData = AudioLowPassFilter.applyLowPassFilter(
//                    wavData = wavData,
//                    sampleRate = SAMPLE_RATE,
//                    cutoffFreq = 1000.0,  // Gunakan 1000 Hz sebagai cutoff untuk menghilangkan noise tinggi
//                    order = 128  // Order yang lebih tinggi untuk transisi yang lebih tajam
//                )
//
//                try {
//                    val outputStream: OutputStream = contentResolver?.openOutputStream(uri!!)!!
//                    if (desiredByteCount <= filteredWavData.size) {
//                        outputStream.write(filteredWavData, 0, desiredByteCount)
//                        Log.e("newBT " + TAG, "run: execute A with filtered data")
//                    } else {
//                        outputStream.write(filteredWavData)
//                        Log.e("newBT " + TAG, "run: execute B with filtered data")
//                    }
//                    outputStream.close()
//                    Log.e("newBT save" + TAG, "Filtered File saved to: $uri")
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
////                try {
////                    val outputStream: OutputStream = contentResolver?.openOutputStream(uri!!)!!
//////                    val outputStream: FileOutputStream = FileOutputStream(file)
////                    if (desiredByteCount <= wavData.size){
//////                                outputStream.write(wavData)
////                        outputStream.write(wavData, 0, desiredByteCount)
////                        Log.e("newBT " + TAG, "run: execute A")
////                    } else {
////                        outputStream.write(wavData)
////                        Log.e("newBT " + TAG, "run: execute B")
////                    }
////                    outputStream.close()
////                    Log.e("newBT save" + TAG, "File saved to: $uri")
//////                    Log.e("newBT save" + TAG, "File saved to: $filePath")
//////                    Toast.makeText(context, "File saved to: $uri", Toast.LENGTH_SHORT).show()
////                } catch (e: IOException){
////                    e.printStackTrace()
////                }
//            }
//
//
//            val receivedInt = ByteBuffer.wrap(mmBuffer, 0, numBytes).order(ByteOrder.LITTLE_ENDIAN).int
//            Log.e("newBT threadconnected", "received: ${receivedInt.toFloat()}")
//            ArrayReceiver.pcgArray.add(receivedInt.toFloat())
//            ArrayReceiver.timeArray.add(elapsedTimeFloat)
//
//
//            val txtFileName = "$title-${System.currentTimeMillis()}.txt"
//            val txtUri = context.contentResolver?.insert(
//                MediaStore.Files.getContentUri("external"),
//                ContentValues().apply {
//                    put(MediaStore.MediaColumns.DISPLAY_NAME, txtFileName)
//                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
//                }
//            )
//
//            try {
//                txtUri?.let { uri ->
//                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
//                        val writer = BufferedWriter(OutputStreamWriter(outputStream))
//                        ArrayReceiver.pcgArray.forEach { value ->
//                            writer.write(value.toInt().toString()) // Write only integer value on each line
//                            writer.newLine()
//                        }
//                        writer.flush()
//                        Log.e("newBT save" + TAG, "Array saved to: $uri")
//                    }
//                }
//            } catch (e: IOException) {
//                Log.e("newBT save" + TAG, "Failed to save array to text file", e)
//            }
//
//
//
////                    updateChart()
//        }
//    }
//    fun cancel(){
//        try {
//            mmSocket.close()
//        } catch (e: IOException){
//            Log.e("Bluetooth record", "Could not close the connect socket", e)
//        }
//    }
//    fun millisToSeconds(millis: Long): Float {
//        return (millis / 1000.0).toFloat().let { "%.3f".format(it).toFloat() }
//    }
//
//    companion object{
//        private const val TAG = "Thread connected"
//        private const val REQUEST_PERMISSION = 1
//        const val SAMPLE_RATE = 24000
//        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
//        // encoding float ok but time not ok
//        //
//        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_FLOAT
////        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
//        val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
////        private const val BUFFER_SIZE = 1024
//    }
//}
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

//fun normalizeAudio(input: ByteArray): ByteArray {
//    // Mencari nilai amplitudo puncak tertinggi
//    var maxAmplitude = 0.0
//    var sumAmplitude = 0.0
//    val sampleCount = input.size / 2
//
//    for (i in input.indices step 2) {
//        val sample = ((input[i + 1].toInt() shl 8) or (input[i].toInt() and 0xFF)).toShort()
//        sumAmplitude += sample.toDouble()
//        maxAmplitude = maxOf(maxAmplitude, Math.abs(sample.toDouble()))
//    }
//
//    // Menghapus DC offset dengan menghitung rata-rata dan menggeser data agar pusat di 0
//    val mean = sumAmplitude / sampleCount
//    val normalizedData = ByteArray(input.size)
//
//    // Menghitung faktor normalisasi untuk mencapai -1.0 dB
//    val targetAmplitude = maxAmplitude * Math.pow(10.0, -1.0 / 20.0) // -1 dB target
//    val normalizationFactor = targetAmplitude / maxAmplitude
//
//    // Mengaplikasikan normalisasi dan menghapus DC offset
//    for (i in input.indices step 2) {
//        val sample = ((input[i + 1].toInt() shl 8) or (input[i].toInt() and 0xFF)).toShort()
//        val centeredSample = (sample - mean).toInt().toShort() // Menghapus DC offset
//        val normalizedSample = (centeredSample * normalizationFactor).toInt()
//            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
//
//        // Menyimpan hasil normalisasi ke dalam array output
//        normalizedData[i] = (normalizedSample.toInt() and 0xFF).toByte()
//        normalizedData[i + 1] = ((normalizedSample.toInt() shr 8) and 0xFF).toByte()
//    }
//
//    return normalizedData
//}


//fun normalizeAudio(input: ByteArray): ByteArray {
//    // Tentukan target amplitudo (untuk rentang -1 hingga 1)
//    val targetAmplitude = Short.MAX_VALUE.toDouble()  // 32767 (nilai maksimum untuk short 16-bit)
//
//    // Mencari nilai amplitudo puncak (maksimal dalam rentang -32768 hingga 32767)
//    var maxAmplitude = 0.0
//    for (i in input.indices step 2) {
//        val sample = ((input[i + 1].toInt() shl 8) or (input[i].toInt() and 0xFF)).toShort()
//        maxAmplitude = maxOf(maxAmplitude, Math.abs(sample.toDouble()))
//    }
//
//    // Faktor normalisasi berdasarkan puncak nilai tertinggi
//    val normalizationFactor = targetAmplitude / maxAmplitude
//
//    // Array untuk menyimpan data yang telah dinormalisasi
//    val normalizedData = ByteArray(input.size)
//
//    // Melakukan normalisasi pada setiap sample
//    for (i in input.indices step 2) {
//        val sample = ((input[i + 1].toInt() shl 8) or (input[i].toInt() and 0xFF)).toShort()
//
//        // Normalisasi sample dan pastikan berada dalam rentang -32768 hingga 32767
//        val normalizedSample = (sample * normalizationFactor).toInt()
//            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
//            .toShort()
//
//        // Menyusun kembali data dalam format byte
//        normalizedData[i] = (normalizedSample.toInt() and 0xFF).toByte()
//        normalizedData[i + 1] = ((normalizedSample.toInt() shr 8) and 0xFF).toByte()
//    }
//
//    return normalizedData
//}

//fun normalizeAudio(input: ByteArray, targetAmplitude: Short = Short.MAX_VALUE): ByteArray {
//    val normalizedData = ByteArray(input.size)
//
//    // Step 1: Cari nilai amplitudo maksimum
//    var maxAmplitude = 0.0
//    for (i in input.indices step 2) {
//        val sample = ((input[i + 1].toInt() shl 8) or (input[i].toInt() and 0xFF)).toShort()
//        maxAmplitude = maxOf(maxAmplitude, Math.abs(sample.toDouble()))
//    }
//
//    // Step 2: Hitung faktor normalisasi
//    if (maxAmplitude == 0.0) return input // Hindari pembagian dengan nol
//    val normalizationFactor = targetAmplitude / maxAmplitude
//
//    // Step 3: Terapkan faktor normalisasi
//    for (i in input.indices step 2) {
//        val sample = ((input[i + 1].toInt() shl 8) or (input[i].toInt() and 0xFF)).toShort()
//        val normalizedSample = (sample * normalizationFactor).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
//        normalizedData[i] = (normalizedSample.toInt() and 0xFF).toByte()
//        normalizedData[i + 1] = ((normalizedSample.toInt() shr 8) and 0xFF).toByte()
//    }
//
//    return normalizedData
//}
