package com.apicta.myoscopealert.bluetooth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.apicta.myoscopealert.R

class ConnectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        val btnConnect = findViewById<Button>(R.id.btnConnect)
//        val btnAccept = findViewById<Button>(R.id.btnAccept)
        btnConnect.setOnClickListener {
            val i = Intent(
                this@ConnectionActivity,
                ConnectActivity::class.java
            )
            startActivity(i)
        }
//        btnAccept.setOnClickListener {
//            val i = Intent(
//                this@ConnectionActivity,
//                AcceptActivity::class.java
//            )
//            startActivity(i)
//        }
    }
}