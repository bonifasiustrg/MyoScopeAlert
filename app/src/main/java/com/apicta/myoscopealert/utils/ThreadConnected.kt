package com.apicta.myoscopealert.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioFormat
import android.media.AudioRecord
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.apicta.myoscopealert.utils.FloatArrayListToWav.shortToBytes
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID


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

//        private val byteArrayOutputStream = ByteArrayOutputStream()

    private var startTime: Long = 0

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

//            activity?.runOnUiThread {
//            }
            val currentTime = System.currentTimeMillis()
            if (startTime == 0L){
                startTime = currentTime
            }
            val elapsedTime = currentTime - startTime
            val elapsedTimeFloat = millisToSeconds(elapsedTime)

            if (elapsedTimeFloat >= 10.0){
                startTime = currentTime
                Log.e("newBT " + TAG, "pcgArray: ${ArrayReceiver.pcgArray}")
                Log.e("newBT " + TAG, "timeArray: ${ArrayReceiver.timeArray}")
                isOn = false

                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "${System.currentTimeMillis()}-$title.wav")
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav")

                val contentResolver = context?.contentResolver
                val uri = contentResolver?.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)

                val desiredByteCount = ArrayReceiver.timeArray.last().toInt() * SAMPLE_RATE
                Log.e("newBT " + TAG, "desiredByCount: $desiredByteCount")
                Log.e("newBT " + TAG, "lastTime: ${ArrayReceiver.timeArray.last().toInt()}")

                data = byteArrayOutputStream.toByteArray()
                val wavData: ByteArray = Wav.generateWavHeader(data, CHANNEL_CONFIG, SAMPLE_RATE, AUDIO_FORMAT)

                try {
                    val outputStream: OutputStream = contentResolver?.openOutputStream(uri!!)!!
                    if (desiredByteCount <= wavData.size){
//                                outputStream.write(wavData)
                        outputStream.write(wavData, 0, desiredByteCount)
                        Log.e("newBT " + TAG, "run: execute A")
                    } else {
                        outputStream.write(wavData)
                        Log.e("newBT " + TAG, "run: execute B")
                    }
                    outputStream.close()
                    Log.e("newBT save" + TAG, "File saved to: $uri")
//                    Toast.makeText(context, "File saved to: $uri", Toast.LENGTH_SHORT).show()
                } catch (e: IOException){
                    e.printStackTrace()
                }
            }

            val receivedInt = ByteBuffer.wrap(mmBuffer, 0, numBytes).order(ByteOrder.LITTLE_ENDIAN).int
            Log.e("newBT Bluetooth record", "received: ${receivedInt.toFloat()}")
            ArrayReceiver.pcgArray.add(receivedInt.toFloat())
            ArrayReceiver.timeArray.add(elapsedTimeFloat)

//                    updateChart()
        }
//            super.run()
    }

    private fun getOutputPath(): String {
        val storageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context?.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)
        } else {
            context?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        }

        val fileName = "${System.currentTimeMillis()}_BluetoothRecording.wav"
        return "${storageDir?.absolutePath}/$fileName"
    }

    private fun saveToMediaStore(filePath: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "BluetoothRecording.wav")
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/x-wav")
        }

        val contentResolver = context?.contentResolver
        val uri = contentResolver?.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { mediaUri ->
            try {
                val outputStream = contentResolver.openOutputStream(mediaUri)
                outputStream?.use { stream ->
                    val file = File(filePath)
                    val fileInputStream = file.inputStream()
                    fileInputStream.copyTo(stream)
                }
                Log.d("BluetoothDataRecorder", "File saved to: $mediaUri")
            } catch (e: IOException) {
                Log.e("BluetoothDataRecorder", "Error saving to MediaStore", e)
            }
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

//    private fun updateChart() {
//        val dataValues: ArrayList<Entry> = ArrayList()
//        for ((time, audio) in ArrayReceiver.timeArray.zip(ArrayReceiver.pcgArray)) {
//            if (time <= 10.0){
//                dataValues.add(Entry(time, audio))
//            }
//        }
//
//        val lineDataset1 = LineDataSet(dataValues, "PCG")
//        lineDataset1.setDrawCircles(false)
//
//        val dataset: ArrayList<ILineDataSet> = ArrayList()
//        dataset.add(lineDataset1)
//
//        binding.signalView.xAxis.isEnabled = false
//        binding.signalView.axisLeft.isEnabled = false
//        binding.signalView.axisRight.isEnabled = false
//
//        binding.signalView.xAxis.axisMinimum = 0f
//        binding.signalView.xAxis.axisMaximum = 10f
//
//        val data = LineData(dataset)
//        binding.signalView.data = data
//        binding.signalView.notifyDataSetChanged()
//        binding.signalView.invalidate()
//    }
    companion object{
        private const val TAG = "Record Patient Screen vm"
        private const val REQUEST_PERMISSION = 1
        private const val SAMPLE_RATE = 24000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        // encoding float ok but time not ok
        //
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_FLOAT
        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
//        private const val BUFFER_SIZE = 1024


    }
}


@SuppressLint("MissingPermission")
class ThreadConnectBTDevice constructor(device: BluetoothDevice, private val context:Context) :
    Thread() {
    private var bluetoothSocket: BluetoothSocket? = null


    private var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    init {
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        var success = false
        try {
            bluetoothSocket!!.connect()
            success = true
        } catch (e: IOException){
            e.printStackTrace()
            Log.e("newBT thread", "Couldn't connect to your device")
//            Toast.makeText(context, "Couldn't connect to your device", Toast.LENGTH_SHORT).show()

            try {
                bluetoothSocket!!.close()
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
        if (success){
//            Toast.makeText(context, "Connection Success!", Toast.LENGTH_SHORT).show()
            Log.e("newBT thread connectt","Connection Success!")
            bluetoothSocket.let { BluetoothSocketHolder.setBluetoothSocket(it!!) }
        }

    }

    fun cancel() {
        try {
            bluetoothSocket!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}