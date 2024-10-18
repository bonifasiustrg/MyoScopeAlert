package com.apicta.myoscopealert.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.viewmodel.BluetoothViewModel
import com.apicta.myoscopealert.ui.viewmodel.StopWatch
import com.apicta.myoscopealert.utils.ThreadConnectBTDevice
import com.apicta.myoscopealert.utils.ThreadConnected
import com.apicta.myoscopealert.utils.checkBtPermission
import com.psp.bluetoothlibrary.Bluetooth
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.ModalBottomSheetM3(
    ctx: Context, isConnect: MutableState<Boolean>,
    stopWatch: StopWatch,
    isStopwatch: MutableState<Boolean>,
    isRecording: MutableState<Boolean>,
    bluetoothViewModel: BluetoothViewModel,
    modifier: Modifier = Modifier
) {
    // Bluetooth object
    val bluetooth: Bluetooth? = Bluetooth(ctx)

    var openBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(/*skipPartiallyExpanded = true*/)

    Button(
        onClick = { openBottomSheet = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xBFFFC107)
        ),
        modifier = modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.BluetoothSearching,
            contentDescription = null,
            modifier = modifier
                .size(32.dp)
                .padding(end = 4.dp),
            tint = primary
        )
        Text(text = "Connect Device", color = Color.Black)
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { openBottomSheet = false },
            dragHandle = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BottomSheetDefaults.DragHandle()
                    Text(text = "Connect Bluetooth", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = modifier.height(10.dp))
                    Divider()
                }
            }
        ) {
            BottomSheetContent(
                onHideButtonClick = {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) openBottomSheet = false
                    }
                },
                bluetooth = bluetooth,
                isConnect,
                stopWatch,
                isStopwatch,
                isRecording,
                bluetoothViewModel,
                ctx,
                modifier
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BottomSheetContent(
    onHideButtonClick: () -> Unit,
    bluetooth: Bluetooth?,
    isConnect: MutableState<Boolean>,
    stopWatch: StopWatch,
    isStopwatch: MutableState<Boolean>,
    isRecording: MutableState<Boolean>,
    bluetoothViewModel: BluetoothViewModel,
    context: Context,
    modifier: Modifier
) {
    val listDetectDevicesString = remember { mutableListOf<String?>() }
    val listDetectBluetoothDevices = remember { mutableListOf<BluetoothDevice?>() }
    val listPairedDevicesString = remember { mutableListOf<String?>() }
    val listPairedBluetoothDevices = remember { mutableListOf<BluetoothDevice?>() }

    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.bluetooth_animation))
    var isScan by remember {mutableStateOf(false)}

    Column(
        modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Button(onClick = {
//
//
//                // Check if not null
//                appContext?.let {
//                    // With user permission
//                    bluetooth!!.turnOnWithPermission(it)
//                    Log.e("turn on bt", "call func")
//                }
//            }) {
//                Text(text = "Turn On")
//            }
            Spacer(modifier = modifier.height(8.dp))
            if (isScan) {
                LottieAnimation(
                    modifier = modifier
                        .size(48.dp)
                        .clickable { isScan = false },
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                )
            } else {
                Button(
                    onClick = {
                        // scan nearby bluetooth devices
                        bluetooth!!.startDetectNearbyDevices()
                        isScan = true
                    },
                    modifier = modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Scan devices")
                }
            }
        }
        Spacer(modifier = modifier.height(8.dp))


        /*SCANNING*/
        // Bluetooth discovery #START
        bluetooth!!.setOnDiscoveryStateChangedListener { state ->
            if (state == Bluetooth.DISCOVERY_STARTED) {
                Log.e("newBT BTBottomSheet", "Memulai pencarian perangkat")
            }
            if (state == Bluetooth.DISCOVERY_FINISHED) {
                Log.e("newBT BTBottomSheet", "Pencarian perangkat selesai")
            }
        }
        // Bluetooth discovery #END


        // Detect nearby bluetooth devices #START
        bluetooth.setOnDetectNearbyDeviceListener { device ->
            // check device is already in list or not
            if (!listDetectDevicesString.contains(device.name)) {
                Log.d(
                    "newBT BTBottomSheet",
                    "Bluetooth device found " + device.name
                )


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
                    return@setOnDetectNearbyDeviceListener
                }
                listDetectDevicesString.add(device.name) // add to list
                Log.e("list bottomsheet", "added ${device.name} to list")
                listDetectBluetoothDevices.add(device)
            }
        }

        // Menggunakan LaunchedEffect untuk melakukan recompose ketika listPairedBluetoothDevices berubah
        LaunchedEffect(listDetectDevicesString, listPairedDevicesString) {
            // Menggunakan coroutine untuk menjalankan recompose
            launch {
                // Melakukan recompose ketika listDetectBluetoothDevices berubah
                listDetectDevicesString.clear() // Bersihkan listPairedBluetoothDevices
                listDetectDevicesString.addAll(listDetectDevicesString) // Tambahkan semua elemen dari listDetectBluetoothDevices ke listPairedBluetoothDevices
                listDetectBluetoothDevices.clear() // Bersihkan listPairedBluetoothDevices
                listDetectBluetoothDevices.addAll(listDetectBluetoothDevices) // Tambahkan semua elemen dari listDetectBluetoothDevices ke listPairedBluetoothDevices
                listPairedDevicesString.clear() // Bersihkan listPairedBluetoothDevices
                listPairedDevicesString.addAll(listPairedDevicesString) // Tambahkan semua elemen dari listDetectBluetoothDevices ke listPairedBluetoothDevices
                listPairedBluetoothDevices.clear() // Bersihkan listPairedBluetoothDevices
                listPairedBluetoothDevices.addAll(listPairedBluetoothDevices) // Tambahkan semua elemen dari listDetectBluetoothDevices ke listPairedBluetoothDevices
            }
        }

        // Get Paired devices list
        getPairedDevices(bluetooth, context, listPairedDevicesString, listPairedBluetoothDevices)
        val isDialogVisible = remember { mutableStateOf(false) }

        Text(text = "Paired Devices", fontSize = 20.sp)
        LazyColumn {
            items(listPairedBluetoothDevices) {
                ListItem(
                    modifier = modifier.clickable {
//                        getPairedDevices(bluetooth, context, listPaired, listPairedBluetoothDevices)
                        isDialogVisible.value = true
//                        val position = listDetectDevicesString.indexOf(it?.name)
//
//                        if (bluetooth.unpairDevice(listPairedBluetoothDevices[position])) {
//                            Log.e("newBT BTBottomSheet", "Unpair successfully $it")
//                            listPairedDevicesString.removeAt(position)
//                            listPairedBluetoothDevices.removeAt(position)
//                        } else {
//                            Log.d("newBT BTBottomSheet", "Unpair failed")
//                        }
                    },
                    headlineContent = {
                        if (it != null) {
                            Text(text = it.name)
                        }
                    },
                    leadingContent = {
                        Icon(imageVector = Icons.Default.SettingsRemote, contentDescription = null)
                    }
                )
            }
        }
        if (isDialogVisible.value) {
            DeviceSelectionDialog(
                listPaired = listPairedDevicesString,
                listPairedBluetoothDevices = listPairedBluetoothDevices,
//                onDeviceSelected = { deviceAddress ->
//                    if (connection.connect(
//                            deviceAddress,
//                            true,
//                            connectionListener,
//                            receiveListener
//                        )
//                    ) {
//                        Log.e("newBT", "Start connection process")
//                        Toast.makeText(context, "Start connection process", Toast.LENGTH_SHORT).show()
//
//                    } else {
//                        Log.e("newBT","Start connection process failed")
//                        Toast.makeText(context, "Start connection process failed", Toast.LENGTH_SHORT).show()
//                    }
//                },
                onDismiss = { isDialogVisible.value = false },
                isDialogVisible = isDialogVisible,
                context,
                bluetoothViewModel,
                onHideButtonClick
            )
        }

        if (listPairedBluetoothDevices.isEmpty()) Text(
            text = "No paired device detected",
            color = Color.Gray
        )
        Spacer(modifier = modifier.height(8.dp))

        Text(text = "Available Devices", fontSize = 20.sp)
        Log.e("list bottomsheet", listDetectDevicesString.toString())
        Log.e("list bottomsheet paired str", listPairedDevicesString.toString())
        Log.e("list bottomsheet paired blt", listPairedBluetoothDevices.toString())
        LazyColumn {
            items(listDetectBluetoothDevices) {
                if (it != null) {
                    ListItem(
                        modifier = modifier.clickable {
                            val position = listDetectDevicesString.indexOf(it.name)
                            if (bluetooth.requestPairDevice(/*listDetectBluetoothDevices[position]*/
                                    it
                                )
                            ) {
//                                Log.d(BluetoothActivity.TAG, "Pair request send successfully $position")
                                Toast.makeText(
                                    context,
                                    "Pair request send successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        headlineContent = { Text(text = it.name) },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.SettingsRemote,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }

        if (listDetectBluetoothDevices.isEmpty()) Text(
            text = "No available device detected",
            color = Color.Gray
        )
        Spacer(modifier = modifier.height(8.dp))



        Button(
            modifier = modifier.align(Alignment.CenterHorizontally),
            onClick = onHideButtonClick
        ) {
            Text(text = "Kembali")
        }
        
        Spacer(modifier = modifier.height(32.dp))
    }
}


/*FUNCTION*/
fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@RequiresApi(Build.VERSION_CODES.S)
fun getPairedDevices(
    bluetooth: Bluetooth?,
    context: Context,
    listPairedDevicesString: MutableList<String?>,
    listPairedBluetoothDevices: MutableList<BluetoothDevice?>
) {
    val devices = bluetooth!!.getPairedDevices()
    if (devices.size > 0) {
        listPairedDevicesString.clear()
        listPairedBluetoothDevices.clear()
        for (device in devices) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted, request it from the user
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    /*REQUEST_BLUETOOTH_PERMISSION*/1
                )
                return
            }
            Log.e("list bottomsheet", "added ${device.name} to list")

//            listPairedDevicesString.add(device.name)
            listPairedDevicesString.add(
                """
                        ${device.name}
                        ${device.address}
                        """.trimIndent()
            )
            listPairedBluetoothDevices.add(device)

            Log.d("newBT BTBottomSheet 1", "Paired device is " + device.name)
        }
    } else {
        Log.d("newBT BTBottomSheet 1", "Paired device list not found")
    }
}

@Composable
fun DeviceSelectionDialog(
    listPaired: MutableList<String?>,
    listPairedBluetoothDevices: MutableList<BluetoothDevice?>,
//    onDeviceSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    isDialogVisible: MutableState<Boolean>,
    context: Context,
    bluetoothViewModel: BluetoothViewModel,
    onHideButtonClick: () -> Unit,

) {
    lateinit var threadConnectBTDevice: ThreadConnectBTDevice
    AlertDialog(
        onDismissRequest = { isDialogVisible.value = false },
        confirmButton = { /*isDialogVisible.value = false*/ },
        title = { Text(text = "Select device") },
        text = {
            Log.e("newBT alert", listPaired.toString())
            Log.e("newBT alert", listPairedBluetoothDevices.toString())
            LazyColumn {
                items(items = listPairedBluetoothDevices) { /*pairedDevice*/device ->
                    ListItem(
                        modifier = Modifier.clickable {

//                            onDeviceSelected(device[1])

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
                                return@clickable
                            }
                            Toast.makeText(
                                context,
                                "Connecting to ${device?.name}..",
                                Toast.LENGTH_SHORT
                            ).show()
                            threadConnectBTDevice = ThreadConnectBTDevice(device!!, context)
                            threadConnectBTDevice.start()
                            Log.e("newBT thread connect", "onClick: ${threadConnectBTDevice.name}")
                            bluetoothViewModel.setBluetoothName(device.name)

                            onDismiss()
                            onHideButtonClick()
                        },
                        headlineContent = { Text(text = /*device[0]*/device!!.name) },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.SettingsRemote,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            if (listPaired.isEmpty()) {
                Column {

                    Text(
                        text = "No paired device detected",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Please check bluetooth connection",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }
            }
        },
    )
}
