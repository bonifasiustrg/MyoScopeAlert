package com.apicta.myoscopealert.bluetooth

import android.Manifest
import android.app.Dialog
import android.bluetooth.BluetoothSocket
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.apicta.myoscopealert.R
import com.psp.bluetoothlibrary.Bluetooth
import com.psp.bluetoothlibrary.BluetoothListener.onConnectionListener
import com.psp.bluetoothlibrary.BluetoothListener.onReceiveListener
import com.psp.bluetoothlibrary.Connection
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.UUID


class ConnectActivity : AppCompatActivity() {
    private val TAG = "bt.BluetoothAppActivity"

    // UI
    private var btnConnect: Button? = null
    private var btnDisconnect: Button? = null
    private var btnSend: Button? = null
    private var btnSendReceive: Button? = null
    private var edtMessage: EditText? = null
    private var txtDisplay: TextView? = null

    // Connection object
    private var connection: Connection? = null

    // ArrayList untuk menyimpan received data
//    private val receivedDataList = ArrayList<Int>()
    private val receivedDataList = ArrayList<Int>()

    override fun onStart() {
        super.onStart()
        if (connection!!.isConnected()) {
            logMsg("initialize receive listener")
            connection!!.setOnReceiveListener(receiveListener)
        }
        logMsg("onStart")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onDestroy() {
        super.onDestroy()
        logMsg("onDestroy")
        disconnect()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        init()

        // initialize connection object
        logMsg("initialize connection object")
        connection = Connection(this)

        // set UUID ( optional )
        // connection.setUUID(your_uuid);

        // ( optional ) *New feature
//        connection.setConnectTimeout(30*1000); // 30 sec connect timeout
        logMsg("Get connect timeout " + connection!!.connectTimeout)

        // ( optional ) *New feature
//        connection.enableConnectTimeout();
        logMsg("Is enable connect timeout " + connection!!.isEnabledConnectTimeout)


        // Connect
        btnConnect!!.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onClick(v: View) {
                deviceAddressAndConnect
            }
        })
        val your_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        connection!!.setUUID(your_uuid);
        // Disconnect
        btnDisconnect!!.setOnClickListener { disconnect() }

//        // Send Data
//        btnSend!!.setOnClickListener(View.OnClickListener {
//            val msg = edtMessage!!.getText().toString().trim { it <= ' ' }
//            if (msg.isEmpty()) {
//                return@OnClickListener
//            }
//            if (connection!!.send(msg)) {
//                logMsg("[TX] $msg")
//                txtDisplay!!.append("\n[TX] $msg")
//                setDisplayMessageScrollBottom()
//            } else {
//                logMsg("[TX] $msg")
//                txtDisplay!!.append("\n[TX] Failed $msg")
//                setDisplayMessageScrollBottom()
//            }
//        })


//        // Send Receive in another activity
//        btnSendReceive!!.setOnClickListener {
//            if (connection!!.isConnected()) {
//                val i = Intent(
//                    this@ConnectActivity,
//                    SendReceiveActivity::class.java
//                )
//                startActivity(i)
//            } else {
//                Toast.makeText(this@ConnectActivity, "Device not connected", Toast.LENGTH_SHORT)
//                    .show()
//                logMsg("Device not connected")
//            }
//        }
    }

    private fun init() {
        btnConnect = findViewById(R.id.btnConnectConnect)
        btnDisconnect = findViewById(R.id.btnConnectDisconnect)
        btnSend = findViewById(R.id.btnConnectSend)
        btnSendReceive = findViewById(R.id.btnConnectSendReceiveConnect)
        edtMessage = findViewById(R.id.edtConnectMessage)
        txtDisplay = findViewById(R.id.txtConnectDisplay)
        txtDisplay!!.setMovementMethod(ScrollingMovementMethod())
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun disconnect() {
        if (connection != null) {
            connection!!.disconnect()
            logMsg("Disconnect manual")
            txtDisplay!!.append("\n[ST] Disconnect manual")
            setDisplayMessageScrollBottom()

            logMsg("All data received: $receivedDataList")
            logMsg("All data received: ${receivedDataList::class.java}")
            logMsg("All data received: ${receivedDataList::class.simpleName}")

            val intArray = receivedDataList.toIntArray()
//            val intArray = intArrayOf(82, 82, 82, 82, 81, 82, 82, 81, 82, 77, 81, 82, 82, 82, 83, 81, 82, 81, 81, 82, 81, 81, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82)
            val contextWrapper = ContextWrapper(this)
            val externalStorage: File = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)!!

            val audioDirPath = externalStorage.absolutePath
            var count = 0
            var outputFile: File
            do {
                val fileName = "output_$count.wav"
                outputFile = File(audioDirPath, fileName)
                count++
            } while (outputFile.exists())
            convertIntArrayToWav(intArray, outputFile)
        } /*else {
            val intArray = intArrayOf(2, 2, 2, 2, 1, 2, 2, 1, 2, -3, 1, 2, 2, 2, 3, 1, 2, 1, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, -20, 4, 0, 23, 8, -11, -25- 0)
            val contextWrapper = ContextWrapper(this)
            val externalStorage: File = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)!!

            val audioDirPath = externalStorage.absolutePath
            var count = 0
            var outputFile: File
            do {
                val fileName = "output_$count.wav"
                outputFile = File(audioDirPath, fileName)
                count++
            } while (outputFile.exists())
            convertIntArrayToWav(intArray, outputFile)
        }*/
    }

    val deviceAddressAndConnect: Unit
        @RequiresApi(Build.VERSION_CODES.S) get() {
            // create dialog box
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select device")
            val modeList = ListView(this)
            val listPaired = ArrayList<String>()
            getPairedDevices(listPaired) // get paired devices
            val modeAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                listPaired
            )
            modeList.setAdapter(modeAdapter)
            builder.setView(modeList)
            val dialog: Dialog = builder.create()
            dialog.show()
            modeList.onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    val device =
                        listPaired[position].split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    Log.e("device trad", "$device -- ${device[0]} -- ${device[1]}")
                    // Connect Bluetooth Device --- device[1] = device mac address
                    if (connection!!.connect(
                            device[1],
                            true,
                            connectionListener,
                            receiveListener
                        )
                    ) {
                        Log.d(TAG, "Start connection process")
                    } else {
                        logMsg("Start connection process failed")
                    }
                    dialog.dismiss()
                }
        }
    private val connectionListener: onConnectionListener = object : onConnectionListener {
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onConnectionStateChanged(socket: BluetoothSocket?, state: Int) {
            when (state) {
                Connection.CONNECTING -> {
                    logMsg("Connecting...")
                    txtDisplay!!.append("\n[ST] Connecting...")
                    setDisplayMessageScrollBottom()
                }

                Connection.CONNECTED -> {
                    logMsg("Connected")
                    txtDisplay!!.append("\n[ST] Connected")
                    setDisplayMessageScrollBottom()
                }

                Connection.DISCONNECTED -> {
                    logMsg("Disconnected")
                    txtDisplay!!.append("\n[ST] Disconnected")
                    setDisplayMessageScrollBottom()
                    disconnect()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun onConnectionFailed(errorCode: Int) {
            when (errorCode) {
                Connection.SOCKET_NOT_FOUND -> {
                    logMsg("Socket not found")
                    txtDisplay!!.append("\n[ST] Socket not found")
                    setDisplayMessageScrollBottom()
                }

                Connection.CONNECT_FAILED -> {
                    logMsg("Connect Failed")
                    txtDisplay!!.append("\n[ST] Connect failed")
                    setDisplayMessageScrollBottom()
                }
            }
            disconnect()
        }
    }
    private val receiveListener =
        onReceiveListener { receivedData ->
            Log.d("BluetoothData", "Received data: $receivedData")
            val receivedBytes = receivedData.toByteArray(Charsets.UTF_8)
            Log.d("BluetoothData byte", "Received data: $receivedBytes")
            val intValue = ByteBuffer.wrap(receivedBytes).getInt()
            Log.d("BluetoothData int", "Received data: $intValue")

            logMsg("[RX] $receivedData")
            txtDisplay!!.append("\n[RX] $receivedData")
            setDisplayMessageScrollBottom()


            // Menambahkan data yang diterima ke dalam ArrayList
            receivedDataList.add(intValue)
//            if (receivedData.trim().isNotEmpty()) {
//                val receivedDataInt = receivedData.replace("\n", "").toIntOrNull()
//                if (receivedDataInt != null) {
//                    receivedDataList.add(receivedDataInt)
//                } else {
//                    Log.e("received data", "String $receivedData gagal dikonversi jadi angka, output null")
//                    // Handle case when receivedData cannot be converted to integer
//                    // Misalnya, Anda dapat menambahkan log atau menampilkan pesan kesalahan
//                }
//            }

        }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getPairedDevices(list: ArrayList<String>) {
        // initialize bluetooth object
        val bluetooth = Bluetooth(this)
        val deviceList = bluetooth.getPairedDevices()
        if (deviceList.size > 0) {
            for (device in deviceList) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                        BluetoothActivity.REQUEST_BLUETOOTH_PERMISSION
                    )
                    return
                }
                list.add(
                    """
                        ${device.name}
                        ${device.address}
                        """.trimIndent()
                )
                Log.d(TAG, "Paired device is " + device.name)
            }
        }
    }

    private fun setDisplayMessageScrollBottom() {
        val layout = txtDisplay!!.layout
        if (layout != null) {
            val scrollDelta = (layout.getLineBottom(txtDisplay!!.lineCount - 1)
                    - txtDisplay!!.scrollY - txtDisplay!!.height)
            if (scrollDelta > 0) txtDisplay!!.scrollBy(0, scrollDelta)
        }
    }

    private fun logMsg(msg: String) {
        Log.d(TAG, msg)
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
}