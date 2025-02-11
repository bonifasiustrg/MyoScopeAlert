package com.apicta.myoscopealert.bt

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.UUID

class ThreadConnectBTDevice(
    private val device: BluetoothDevice,
    private val context: Context,
    private val onConnected: (String) -> Unit // Callback untuk memberi tahu koneksi sukses
) : Thread() {
    private var bluetoothSocket: BluetoothSocket? = null
    private var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    init {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
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

            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        var success = false
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
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
            bluetoothSocket!!.connect()
            success = true
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("newBT thread", "Couldn't connect to your device")
            try {
                bluetoothSocket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (success) {
            Log.e("newBT thread", "Connection Success!")
            bluetoothSocket?.let {
                BluetoothSocketHolder.setBluetoothSocket(it)
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                onConnected(device.name) // Mengirim nama perangkat ke UI setelah koneksi berhasil
            }
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
