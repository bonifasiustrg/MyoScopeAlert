package com.apicta.myoscopealert.bt

import android.bluetooth.BluetoothSocket

object BluetoothSocketHolder {
    private var bluetoothSocket: BluetoothSocket? = null

    fun setBluetoothSocket(socket: BluetoothSocket) {
        bluetoothSocket = socket
//        Log.d("BluetoothSocketHolder", "setBluetoothSocket: $bluetoothSocket")
    }

    fun getBluetoothSocket(): BluetoothSocket? {
        return bluetoothSocket
    }


}
