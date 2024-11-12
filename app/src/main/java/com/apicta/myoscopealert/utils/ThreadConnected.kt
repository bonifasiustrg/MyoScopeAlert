package com.apicta.myoscopealert.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.os.Build
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
//            firFilterProcessor.processAudio(audioData)
//            ArrayReceiver.pcgArray.add(audioData.toFloat())
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
//    private fun createLowPassFIR(order: Int, cutoffFreq: Double, sampleRate: Int): DoubleArray {
//        val coefficients = DoubleArray(order + 1)
//        val fc = cutoffFreq / sampleRate
//        for (i in 0..order) {
//            coefficients[i] = if (i == order / 2) {
//                2 * Math.PI * fc
//            } else {
//                Math.sin(2 * Math.PI * fc * (i - order / 2)) / (i - order / 2) *
//                        (0.54 - 0.46 * Math.cos(2 * Math.PI * i / order))
//            }
//        }
//        return coefficients
//    }
//
//    inner class FIRFilterProcessor(private val coefficients: DoubleArray) : AudioProcessor {
//        private val buffer = DoubleArray(coefficients.size)
//        private var bufferIndex = 0
//
//        fun processAudio(audioSample: Float): Float {
//            buffer[bufferIndex] = audioSample.toDouble()
//            var output = 0.0
//
//            var index = bufferIndex
//            for (j in coefficients.indices) {
//                output += coefficients[j] * buffer[index]
//                index = if (index == 0) buffer.size - 1 else index - 1
//            }
//
//            bufferIndex = (bufferIndex + 1) % buffer.size
//            return output.toFloat()
//        }
//
//        override fun process(audioEvent: AudioEvent?): Boolean = true
//        override fun processingFinished() {}
//    }
//}


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

        while (isOn){
            try {
                numBytes = mmInStream.read(mmBuffer)
                byteArrayOutputStream.write(mmBuffer, 0, numBytes)

            } catch (e: IOException){
                Log.e("newBT Bluetooth record", "Input stream was disconnected", e)
                break
            }
            Log.e("newBT Bluetooth record", "numBytes: $numBytes")
            val currentTime = System.currentTimeMillis()
            if (startTime == 0L){
                startTime = currentTime
            }
            val elapsedTime = currentTime - startTime
            val elapsedTimeFloat = millisToSeconds(elapsedTime)

            if (elapsedTimeFloat >= 12/*30.0*/){
                startTime = currentTime
                Log.e("newBT " + TAG, "pcgArray: ${ArrayReceiver.pcgArray}")
                Log.e("newBT " + TAG, "nilai maksimum: ${ArrayReceiver.pcgArray.maxOrNull()}")
//                Log.e("newBT " + TAG, "timeArray: ${ArrayReceiver.timeArray}")
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
                Log.e("newBT " + TAG, "lastTime: ${ArrayReceiver.timeArray.last().toInt()}")

//                val contextWrapper = ContextWrapper(context)
//                val externalStorage: File = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)!!
//
//                val audioDirPath = externalStorage.absolutePath
//                val fileName = "${System.currentTimeMillis()}-$title.wav"
//                val filePath = "$audioDirPath/$fileName"
//
//                val file = File(filePath)
//                file.createNewFile()

                data = byteArrayOutputStream.toByteArray()
                val gainFactor = 3.0f // Coba nilai yang lebih tinggi
//                val slowdownFactor = 1f // Nilai > 1 akan memperlambat audio
                val adjustedAudioData = adjustVolumeWithCompression(data, gainFactor)
//                val slowedAudioData = slowDownAudio(adjustedAudioData, slowdownFactor)
//                val adjustedSampleRate = (SAMPLE_RATE / slowdownFactor).toInt()

//                val wavData: ByteArray = Wav.generateWavHeader(slowedAudioData, CHANNEL_CONFIG, SAMPLE_RATE, AUDIO_FORMAT)
                val wavData: ByteArray = Wav.generateWavHeader(adjustedAudioData, CHANNEL_CONFIG, SAMPLE_RATE, AUDIO_FORMAT)

                val filteredWavData = AudioLowPassFilter.applyLowPassFilter(
                    wavData = wavData,
                    sampleRate = SAMPLE_RATE,
                    cutoffFreq = 1000.0,  // Gunakan 1000 Hz sebagai cutoff untuk menghilangkan noise tinggi
                    order = 128  // Order yang lebih tinggi untuk transisi yang lebih tajam
                )

                try {
                    val outputStream: OutputStream = contentResolver?.openOutputStream(uri!!)!!
                    if (desiredByteCount <= filteredWavData.size) {
                        outputStream.write(filteredWavData, 0, desiredByteCount)
                        Log.e("newBT " + TAG, "run: execute A with filtered data")
                    } else {
                        outputStream.write(filteredWavData)
                        Log.e("newBT " + TAG, "run: execute B with filtered data")
                    }
                    outputStream.close()
                    Log.e("newBT save" + TAG, "Filtered File saved to: $uri")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

//                try {
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
////                    Log.e("newBT save" + TAG, "File saved to: $filePath")
////                    Toast.makeText(context, "File saved to: $uri", Toast.LENGTH_SHORT).show()
//                } catch (e: IOException){
//                    e.printStackTrace()
//                }
            }


            val receivedInt = ByteBuffer.wrap(mmBuffer, 0, numBytes).order(ByteOrder.LITTLE_ENDIAN).int
            Log.e("newBT threadconnected", "received: ${receivedInt.toFloat()}")
            ArrayReceiver.pcgArray.add(receivedInt.toFloat())
            ArrayReceiver.timeArray.add(elapsedTimeFloat)

//                    updateChart()
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
        private const val TAG = "Thread connected"
        private const val REQUEST_PERMISSION = 1
        const val SAMPLE_RATE = 24000
//        const val SAMPLE_RATE = 44100
//        const val SAMPLE_RATE = 22050
//        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        // encoding float ok but time not ok
        //
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_FLOAT
//        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
//        private const val BUFFER_SIZE = 1024
    }
}
