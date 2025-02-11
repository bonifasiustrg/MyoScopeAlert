package com.apicta.myoscopealert.bt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BluetoothViewModel: ViewModel() {
    private val _isConnect = MutableStateFlow(false)
    val isConnect: StateFlow<Boolean> = _isConnect.asStateFlow()

    private val _bluetoothName = MutableLiveData<String?>()
    val bluetoothName: LiveData<String?> = _bluetoothName

    fun setBluetoothName(deviceName: String) {
        _bluetoothName.postValue(deviceName)
        _isConnect.value =true
        Log.d(TAG, "Connected to: $deviceName")
    }

    fun setConnectionStatus(isConnected: Boolean) {
        viewModelScope.launch {
            _isConnect.emit(isConnected)
        }
        if (!isConnected) {
            _bluetoothName.postValue(null) // Hapus nama jika disconnected
        }
    }

    companion object {
        private const val TAG = "BluetoothViewModel"
    }
}

