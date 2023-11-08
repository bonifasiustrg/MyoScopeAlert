package com.apicta.myoscopealert.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.apicta.myoscopealert.R
import com.psp.bluetoothlibrary.Bluetooth
import com.psp.bluetoothlibrary.BluetoothListener.onDevicePairListener


class BluetoothActivity : AppCompatActivity() {
    // UI
    var btnTurnOn: Button? = null
    var btnTurnOff: Button? = null
    var btnScan: Button? = null
    var listViewPairedDevices: ListView? = null
    var listViewDetectDevices: ListView? = null

    // List For paired devices and detect devices
    var listDetectDevicesString: ArrayList<String>? = null
    var listPairedDevicesString: ArrayList<String>? = null
    var listDetectBluetoothDevices: ArrayList<BluetoothDevice>? = null
    var listPairedBluetoothDevices: ArrayList<BluetoothDevice>? = null
    var adapterDetectBluetoothDevices: ArrayAdapter<String>? = null
    var adapterPairedBluetoothDevices: ArrayAdapter<String>? = null

    // Bluetooth object
    private var bluetooth: Bluetooth? = null

    // optional
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Bluetooth.BLUETOOTH_ENABLE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Bluetooth on")
            }
            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Bluetooth turn on dialog canceled")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        init()

        // Request fine location permission
        checkRunTimePermission()

        /*
        <uses-permission android:name="android.permission.BLUETOOTH" />
        <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

        - add this in your manifests file
         */

        // initialize bluetooth
        bluetooth = Bluetooth(this)

        // check bluetooth is supported or not
        Log.d(TAG, "Bluetooth is supported " + Bluetooth.isBluetoothSupported())
        Log.d(TAG, "Bluetooth is on " + bluetooth!!.isOn())
        Log.d(TAG, "Bluetooth is discovering " + bluetooth!!.isDiscovering())

        // turn on bluetooth
        btnTurnOn!!.setOnClickListener {
            // With user permission
            bluetooth!!.turnOnWithPermission(this@BluetoothActivity)
            // Without user permission
            // bluetooth.turnOnWithoutPermission();
        }

        // turn off bluetooth
        btnTurnOff!!.setOnClickListener {
            bluetooth!!.turnOff() // turn off
        }


        // Bluetooth discovery #START
        bluetooth!!.setOnDiscoveryStateChangedListener { state ->
            if (state == Bluetooth.DISCOVERY_STARTED) {
                Log.d(TAG, "Discovery started")
            }
            if (state == Bluetooth.DISCOVERY_FINISHED) {
                Log.d(TAG, "Discovery finished")
            }
        }
        // Bluetooth discovery #END

        // Detect nearby bluetooth devices #START
        bluetooth!!.setOnDetectNearbyDeviceListener { device ->
            // check device is already in list or not
            if (!listDetectDevicesString!!.contains(device.name)) {
                Log.d(
                    TAG,
                    "Bluetooth device found " + device.name
                )
                listDetectDevicesString!!.add(device.name) // add to list
                listDetectBluetoothDevices!!.add(device)
                adapterDetectBluetoothDevices!!.notifyDataSetChanged()
            }
        }
        btnScan!!.setOnClickListener { // clear all devices list
            clearDetectDeviceList()
            // scan nearby bluetooth devices
            bluetooth!!.startDetectNearbyDevices()
        }
        // Detect nearby bluetooth devices #END

        // Bluetooth Pairing #START
        bluetooth!!.setOnDevicePairListener(object : onDevicePairListener {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onDevicePaired(device: BluetoothDevice) {
                if (ActivityCompat.checkSelfPermission(
                        this@BluetoothActivity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@BluetoothActivity,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                        REQUEST_BLUETOOTH_PERMISSION
                    )
                    return
                }
                Log.d(TAG, device.name + " Paired successfull")
                Toast.makeText(
                    this@BluetoothActivity,
                    device.name + " Paired successfull",
                    Toast.LENGTH_SHORT
                ).show()

                // remove device from detect device list
                listDetectDevicesString!!.remove(device.name)
                listDetectBluetoothDevices!!.remove(device)
                adapterDetectBluetoothDevices!!.notifyDataSetChanged()

                // add device to paired device list
                listPairedDevicesString!!.add(device.name)
                listPairedBluetoothDevices!!.add(device)
                adapterPairedBluetoothDevices!!.notifyDataSetChanged()
            }

            @RequiresApi(Build.VERSION_CODES.S)
            override fun onCancelled(device: BluetoothDevice) {
                if (ActivityCompat.checkSelfPermission(
                        this@BluetoothActivity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is not granted, request it from the user
                    ActivityCompat.requestPermissions(
                        this@BluetoothActivity,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                        REQUEST_BLUETOOTH_PERMISSION
                    )
                    return
                }
                Toast.makeText(
                    this@BluetoothActivity,
                    device.name + " Paired failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        listViewDetectDevices!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                if (bluetooth!!.requestPairDevice(listDetectBluetoothDevices!![position])) {
                    Log.d(TAG, "Pair request send successfully")
                    Toast.makeText(
                        this@BluetoothActivity,
                        "Pair request send successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        // Bluetooth Pairing #END


        // Get Paired devices list
        getPairedDevices()


        // Unpair bluetooh device #START
        listViewPairedDevices!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                if (bluetooth!!.unpairDevice(listPairedBluetoothDevices!![position])) {
                    Log.d(TAG, "Unpair successfully")
                    listPairedDevicesString!!.removeAt(position)
                    listPairedBluetoothDevices!!.removeAt(position)
                    adapterPairedBluetoothDevices!!.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "Unpair failed")
                }
            }
        // Unpair bluetooh device #END
    }

    override fun onStop() {
        super.onStop()
        bluetooth!!.onStop()
        Log.d(TAG, "OnStop")
    }

    private fun init() {
        btnTurnOn = findViewById(R.id.btnTurnOn)
        btnTurnOff = findViewById(R.id.btnTurnOff)
        btnScan = findViewById(R.id.btnScan)
        listViewPairedDevices = findViewById(R.id.listViewPairedDevice)
        listViewDetectDevices = findViewById(R.id.listViewDetectDevice)
        listDetectDevicesString = ArrayList()
        listPairedDevicesString = ArrayList()
        listDetectBluetoothDevices = ArrayList()
        listPairedBluetoothDevices = ArrayList()
        adapterDetectBluetoothDevices = ArrayAdapter<String>(
            this, R.layout.device_item,
            listDetectDevicesString!!
        )
        adapterPairedBluetoothDevices = ArrayAdapter<String>(
            this, R.layout.device_item,
            listPairedDevicesString!!
        )
        listViewDetectDevices!!.setAdapter(adapterDetectBluetoothDevices)
        listViewPairedDevices!!.setAdapter(adapterPairedBluetoothDevices)
    }

//    private val pairedDevices: Unit
//        get() {
//            val devices = bluetooth!!.getPairedDevices()
//            if (devices.size > 0) {
//                for (device in devices) {
//                    if (ActivityCompat.checkSelfPermission(
//                            this,
//                            Manifest.permission.BLUETOOTH_CONNECT
//                        ) != PackageManager.PERMISSION_GRANTED
//                    ) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return
//                    }
//                    listPairedDevicesString!!.add(device.name)
//                    listPairedBluetoothDevices!!.add(device)
//                    Log.d(TAG, "Paired device is " + device.name)
//                }
//            } else {
//                Log.d(TAG, "Paired device list not found")
//            }
//        }
@RequiresApi(Build.VERSION_CODES.S)
private fun getPairedDevices() {
    val devices = bluetooth!!.getPairedDevices()
    if (devices.size > 0) {
        for (device in devices) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted, request it from the user
                ActivityCompat.requestPermissions(
                    this@BluetoothActivity,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    REQUEST_BLUETOOTH_PERMISSION
                )
                return
            }
            listPairedDevicesString!!.add(device.name)
            listPairedBluetoothDevices!!.add(device)
            Log.d(TAG, "Paired device is " + device.name)
        }
    } else {
        Log.d(TAG, "Paired device list not found")
    }
}

    fun checkRunTimePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Fine location permission is already granted")
        } else {
            Log.d(TAG, "request fine location permission")
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                10
            )
        }
    }

    private fun clearDetectDeviceList() {
        if (listDetectDevicesString!!.size > 0) {
            listDetectDevicesString!!.clear()
        }
        if (listDetectBluetoothDevices!!.size > 0) {
            listDetectBluetoothDevices!!.clear()
        }
        adapterDetectBluetoothDevices!!.notifyDataSetChanged()
    }

    companion object {
        const val TAG = "psp.BluetoothAct"
        const val REQUEST_BLUETOOTH_PERMISSION = 1
    }

}