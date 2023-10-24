package com.apicta.myoscopealert.bluetooth

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.apicta.myoscopealert.R
import com.psp.bluetoothlibrary.SendReceive

class SendReceiveActivity : AppCompatActivity() {
    private val TAG = "psp.SendRecAct"

    // UI
    private var btnSend: Button? = null
    private var edtMessage: EditText? = null
    private var txtDisplay: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_receive)
        init()

        // Receive listner
        SendReceive.getInstance().setOnReceiveListener { receivedData ->
            logMsg("[RX] $receivedData")
            txtDisplay!!.append("\n[RX] $receivedData")
            setDisplayMessageScrollBottom()
        }


        // Send data
        btnSend!!.setOnClickListener(View.OnClickListener {
            val msg = edtMessage!!.getText().toString().trim { it <= ' ' }
            if (msg.isEmpty()) {
                return@OnClickListener
            }
            if (SendReceive.getInstance().send(msg)) {
                logMsg("[TX] $msg")
                txtDisplay!!.append("\n[TX] $msg")
                setDisplayMessageScrollBottom()
            } else {
                logMsg("[TX] Failed $msg")
                txtDisplay!!.append("\n[TX] Failed $msg")
                setDisplayMessageScrollBottom()
            }
        })
    }

    private fun init() {
        btnSend = findViewById(R.id.btnSendSendRec)
        edtMessage = findViewById(R.id.edtMessageSendRec)
        txtDisplay = findViewById(R.id.txtDisplaySendRec)
        txtDisplay!!.setMovementMethod(ScrollingMovementMethod())
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