package com.apicta.myoscopealert.bluetooth

import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apicta.myoscopealert.R
import com.psp.bluetoothlibrary.BluetoothListener.onConnectionListener
import com.psp.bluetoothlibrary.BluetoothListener.onReceiveListener
import com.psp.bluetoothlibrary.Connection

class AcceptActivity : AppCompatActivity() {
    private val TAG = "psp.AcceptAct"

    // UI
    var btnListen: Button? = null
    var btnDisconnect: Button? = null
    var btnSend: Button? = null
    var btnSendReceive: Button? = null
    var edtMessage: EditText? = null
    var txtDisplay: TextView? = null

    // Connection
    private var connection: Connection? = null
    override fun onStart() {
        super.onStart()
        if (connection!!.isConnected()) {
            logMsg("initialize receive listener")
            connection!!.setOnReceiveListener(receiveListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logMsg("onDestroy")
        disconnect()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept)
        init()

        // initialize connection object
        logMsg("initialize connection object")
        connection = Connection(this)

        // set UUID ( optional )
        // connection.setUUID(your_uuid);

        // Send data
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
                logMsg("[TX] Failed $msg")
                txtDisplay!!.append("\n[TX] Failed$msg")
                setDisplayMessageScrollBottom()
            }
        })


        // Send Receive in another activity
        btnSendReceive!!.setOnClickListener {
            if (connection!!.isConnected()) {
                val i = Intent(
                    this@AcceptActivity,
                    SendReceiveActivity::class.java
                )
                startActivity(i)
            } else {
                Toast.makeText(this@AcceptActivity, "Device not connected", Toast.LENGTH_SHORT)
                    .show()
                logMsg("Device not connected")
            }
        }

        // Disconnect
        btnDisconnect!!.setOnClickListener { disconnect() }


        // Listen connection
        btnListen!!.setOnClickListener {
            if (connection!!.accept(true, connectionListener, receiveListener)) {
                logMsg("Start listening process")
            } else {
                logMsg("Start listening process failed")
            }
        }
    }

    private val connectionListener: onConnectionListener = object : onConnectionListener {
        override fun onConnectionStateChanged(socket: BluetoothSocket, state: Int) {
            when (state) {
                Connection.START_LISTENING -> {
                    logMsg("Start Listening...")
                    txtDisplay!!.append("\n[ST] Start listening...")
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
                Connection.SERVER_SOCKET_NOT_FOUND -> {
                    logMsg("Server socket not found")
                    txtDisplay!!.append("\n[ST] Server socket not found")
                    setDisplayMessageScrollBottom()
                }

                Connection.ACCEPT_FAILED -> {
                    logMsg("Accept failed")
                    txtDisplay!!.append("\n[ST] Accept failed")
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

    private fun init() {
        btnListen = findViewById(R.id.btnAcceptListen)
        btnDisconnect = findViewById(R.id.btnAcceptDisconnect)
        btnSend = findViewById(R.id.btnAcceptSend)
        btnSendReceive = findViewById(R.id.btnAcceptSendReceive)
        edtMessage = findViewById(R.id.edtAcceptMessage)
        txtDisplay = findViewById(R.id.txtAcceptDisplay)
        txtDisplay!!.setMovementMethod(ScrollingMovementMethod())
    }

    private fun disconnect() {
        if (connection != null) {
            logMsg("Disconnect manual")
            connection!!.disconnect()
            txtDisplay!!.append("\n[ST] Disconnect manual")
            setDisplayMessageScrollBottom()
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