package com.apicta.myoscopealert.bluetooth

import android.Manifest
import android.app.Dialog
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.apicta.myoscopealert.R
import com.psp.bluetoothlibrary.Bluetooth
import com.psp.bluetoothlibrary.BluetoothListener.onConnectionListener
import com.psp.bluetoothlibrary.BluetoothListener.onReceiveListener
import com.psp.bluetoothlibrary.Connection
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
    override fun onStart() {
        super.onStart()
        if (connection!!.isConnected()) {
            logMsg("initialize receive listener")
            connection!!.setOnReceiveListener(receiveListener)
        }
        logMsg("onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        logMsg("onDestroy")
        disconnect()
    }

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
            override fun onClick(v: View) {
                deviceAddressAndConnect
            }
        })
        val your_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        connection!!.setUUID(your_uuid);
        // Disconnect
        btnDisconnect!!.setOnClickListener { disconnect() }

        // Send Data
        btnSend!!.setOnClickListener(View.OnClickListener {
            val msg = edtMessage!!.getText().toString().trim { it <= ' ' }
            if (msg.isEmpty()) {
                return@OnClickListener
            }
            if (connection!!.send(msg)) {
                logMsg("[TX] $msg")
                txtDisplay!!.append("\n[TX] $msg")
                setDisplayMessageScrollBottom()
            } else {
                logMsg("[TX] $msg")
                txtDisplay!!.append("\n[TX] Failed $msg")
                setDisplayMessageScrollBottom()
            }
        })


        // Send Receive in another activity
        btnSendReceive!!.setOnClickListener {
            if (connection!!.isConnected()) {
                val i = Intent(
                    this@ConnectActivity,
                    SendReceiveActivity::class.java
                )
                startActivity(i)
            } else {
                Toast.makeText(this@ConnectActivity, "Device not connected", Toast.LENGTH_SHORT)
                    .show()
                logMsg("Device not connected")
            }
        }
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

    private fun disconnect() {
        if (connection != null) {
            connection!!.disconnect()
            logMsg("Disconnect manual")
            txtDisplay!!.append("\n[ST] Disconnect manual")
            setDisplayMessageScrollBottom()
        }
    }

    private val deviceAddressAndConnect: Unit
        private get() {
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
            logMsg("[RX] $receivedData")
            txtDisplay!!.append("\n[RX] $receivedData")
            setDisplayMessageScrollBottom()
        }

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
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
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
}