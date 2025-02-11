package com.apicta.myoscopealert.bt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothReceiver(private val bluetoothViewModel: BluetoothViewModel) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                Log.d(TAG, "Bluetooth Connected")
                bluetoothViewModel.setConnectionStatus(true)
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                Log.d(TAG, "Bluetooth Disconnected")
                bluetoothViewModel.setConnectionStatus(false)
            }
            BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.STATE_DISCONNECTED)
                val isConnected = state == BluetoothAdapter.STATE_CONNECTED
                bluetoothViewModel.setConnectionStatus(isConnected)
            }
        }
    }

    companion object {
        private const val TAG = "BluetoothReceiver"
    }
}
