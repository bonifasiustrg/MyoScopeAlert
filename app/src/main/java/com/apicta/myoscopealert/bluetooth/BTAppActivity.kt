package com.apicta.myoscopealert.bluetooth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.apicta.myoscopealert.R

class BTAppActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_btapp)
//        checkBluetoothPermissions()
        val btnBluetoothActivity = findViewById<Button>(R.id.btnBluetoothActivity)
        val btnConnectionActivity = findViewById<Button>(R.id.btnConnectionActivity)


//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
//                BluetoothActivity.MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT
//            )
//        }

        btnBluetoothActivity.setOnClickListener {
            val i = Intent(
                this@BTAppActivity,
                BluetoothActivity::class.java
            )
            startActivity(i)
        }


        btnConnectionActivity.setOnClickListener {
            val i = Intent(
                this@BTAppActivity,
                ConnectionActivity::class.java
            )
            startActivity(i)
        }
    }


//    @RequiresApi(Build.VERSION_CODES.S)
//    private fun checkBluetoothPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
//            // Jika izin Bluetooth belum diberikan, minta izin
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.BLUETOOTH),
//                MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT
//            )
//        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            // Jika izin Bluetooth Scan belum diberikan, minta izin
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.BLUETOOTH_SCAN),
//                MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN
//            )
//        } else {
//            // Izin sudah diberikan, lanjutkan dengan operasi Bluetooth Anda di sini
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when (requestCode) {
//            MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT, MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Izin diberikan, lanjutkan dengan operasi Bluetooth Anda di sini
//                } else {
//                    // Izin ditolak, tanggapi sesuai kebutuhan aplikasi Anda (tampilkan pesan, nonaktifkan fitur, dll.)
//                }
//            }
//            else -> {
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//            }
//        }
//    }
}