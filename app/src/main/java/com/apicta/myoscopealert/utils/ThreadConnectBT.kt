package com.apicta.myoscopealert.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class ThreadConnectBTDevice constructor(device: BluetoothDevice, private val context: Context) :
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