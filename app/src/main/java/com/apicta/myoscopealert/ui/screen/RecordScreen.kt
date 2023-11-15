package com.apicta.myoscopealert.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.viewmodel.BluetoothViewModel
import com.apicta.myoscopealert.ui.viewmodel.StopWatch
import com.apicta.myoscopealert.utils.BluetoothSocketHolder
import com.apicta.myoscopealert.utils.ThreadConnectBTDevice
import com.apicta.myoscopealert.utils.ThreadConnected
import com.apicta.myoscopealert.utils.checkBtPermission
import com.apicta.myoscopealert.utils.convertIntArrayToWav
import com.apicta.myoscopealert.utils.getCurrentTime
import com.psp.bluetoothlibrary.Bluetooth
import com.psp.bluetoothlibrary.BluetoothListener
import com.psp.bluetoothlibrary.Connection
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RecordScreen(navController: NavHostController) {
    lateinit var threadConnectBTDevice: ThreadConnectBTDevice
    val bluetoothViewModel: BluetoothViewModel = viewModel()
    var myThreadConnected: ThreadConnected? = null

    val context = LocalContext.current
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.recording))

    var title by rememberSaveable { mutableStateOf("defaultname")}
    val errorTitle = "Text input too long"
    var isErrorTitle by rememberSaveable { mutableStateOf(false)}


    var showResult by remember { mutableStateOf(false) }
    val isRecording = remember { mutableStateOf(false) }
    val isStopwatch = remember {
        mutableStateOf(false)
    }
    val filePath =
        "/storage/emulated/0/Android/data/com.apicta.myoscopealert/files/Recordings/Record17Oct.wav"

//    var receivedDataList = remember { mutableStateOf(emptyList<Int>()) }
    var receivedDataList by remember { mutableStateOf(mutableListOf<Int>()) }

    val isDialogVisible = remember { mutableStateOf(false) }

    val stopWatch = remember { StopWatch() }

    // Connection object
    val connection by remember { mutableStateOf(Connection(context)) }
    val your_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    connection.setUUID(your_uuid);
    val isConnect = remember {
        mutableStateOf(false)
    }
//    DisposableEffect(Unit) {
//        onDispose {
//            connection.disconnect()
//        }
//    }
    // Bluetooth object
    var bluetooth: Bluetooth? = null
    bluetooth = Bluetooth(context)
    val listPaired = remember { mutableListOf<String?>() }

    val listPairedBluetoothDevices = remember { mutableListOf<BluetoothDevice?>() }


    @RequiresApi(Build.VERSION_CODES.S)
    fun disconnect() {
        if (connection != null) {
            connection!!.disconnect()
            logMsg("Disconnect manual")
//                txtDisplay!!.append("\n[ST] Disconnect manual")
//                setDisplayMessageScrollBottom()

            logMsg("All data received: $receivedDataList")
            logMsg("All data received: ${receivedDataList::class.java}")
            logMsg("All data received: ${receivedDataList::class.simpleName}")

            val intArray = receivedDataList.toIntArray()
//            val intArray = intArrayOf(82, 82, 82, 82, 81, 82, 82, 81, 82, 77, 81, 82, 82, 82, 83, 81, 82, 81, 81, 82, 81, 81, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82)
            val contextWrapper = ContextWrapper(context)
            val externalStorage: File = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)!!

            val audioDirPath = externalStorage.absolutePath
            var count = 0
            var outputFile: File
            do {
                val fileName = "RecordOutput_$count.wav"
                outputFile = File(audioDirPath, fileName)
                count++
            } while (outputFile.exists())
            convertIntArrayToWav(intArray, outputFile)
        }
    }
    val connectionListener: BluetoothListener.onConnectionListener = object :
        BluetoothListener.onConnectionListener {
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onConnectionStateChanged(socket: BluetoothSocket?, state: Int) {
            when (state) {
                Connection.CONNECTING -> {
                    logMsg("Connecting...")
                    Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show()
                }

                Connection.CONNECTED -> {
                    logMsg("Connected")
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()

                }

                Connection.DISCONNECTED -> {
                    logMsg("Disconnected")

                    disconnect()
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun onConnectionFailed(errorCode: Int) {
            when (errorCode) {
                Connection.SOCKET_NOT_FOUND -> {
                    logMsg("Socket not found")
//                    txtDisplay!!.append("\n[ST] Socket not found")
//                    setDisplayMessageScrollBottom()
                    Toast.makeText(context, "Socket not found", Toast.LENGTH_SHORT).show()

                }

                Connection.CONNECT_FAILED -> {
                    logMsg("Connect Failed")
//                    txtDisplay!!.append("\n[ST] Connect failed")
//                    setDisplayMessageScrollBottom()
                    Toast.makeText(context, "Connect Failed", Toast.LENGTH_SHORT).show()

                }
            }
            connection.disconnect()
        }
    }



    val receiveListener =
        BluetoothListener.onReceiveListener { receivedData ->
            Log.d("BluetoothData", "Received data: $receivedData")
            val receivedBytes = receivedData.toByteArray(Charsets.UTF_8)
            Log.d("BluetoothData byte", "Received data: $receivedBytes")
            val intValue = ByteBuffer.wrap(receivedBytes).getInt()
            Log.d("BluetoothData int", "Received data: $intValue")

            logMsg("[RX] $receivedData")
//            txtDisplay!!.append("\n[RX] $receivedData")
//            setDisplayMessageScrollBottom()


            // Menambahkan data yang diterima ke dalam ArrayList
            Toast.makeText(context, "Receiving data...", Toast.LENGTH_SHORT).show()

            receivedDataList.add(intValue)
            // Trigger recomposition by assigning the updated list to the mutableStateOf
            receivedDataList = receivedDataList.toMutableList()
            // Perform other actions with the received integer value if needed

//            if (receivedData.trim().isNotEmpty()) {
//                val receivedDataInt = receivedData.replace("\n", "").toIntOrNull()
//                if (receivedDataInt != null) {
//                    receivedDataList.add(receivedDataInt)
//                } else {
//                    Log.e("received data", "String $receivedData gagal dikonversi jadi angka, output null")
//                    // Handle case when receivedData cannot be converted to integer
//                    // Misalnya, Anda dapat menambahkan log atau menampilkan pesan kesalahan
//                }
//            }

        }


    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        TextField(
            value = title,
            onValueChange = { title = it
                if (title!="") isErrorTitle = false
                            },
            placeholder = {
                Text(
                    text = "Masukkan Nama File",
                    color = Color.LightGray,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = poppins,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            },
            modifier = Modifier
                .background(color = Color.Transparent)
                .align(Alignment.CenterHorizontally),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
            ),
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontFamily = poppins,
            ),
            singleLine = true,
            isError = isErrorTitle,
            supportingText = {
                if (isErrorTitle) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Please fill the title name",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (isErrorTitle)
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
            },
            keyboardActions = KeyboardActions { isErrorTitle = title.isNotEmpty() }
        )

        Spacer(modifier = Modifier.height(32.dp))


//        if (showResult) {
////                ProcessWavFileData(filePath, context)
//            SetUpChart(ctx = context)
//        } else {
//            SetUpChart(context)
//        }
        if (/*isConnect.value && !showResult*/isRecording.value && isStopwatch.value) {
            Column {
                LottieAnimation(
                    modifier = Modifier
                        .fillMaxHeight(0.4f)
                        .fillMaxWidth(),
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                )
            }
        } else {
            SetUpChart(context)

        }

        Spacer(modifier = Modifier.height(16.dp))

//        STOPWATCH
        Text(
            text = stopWatch.formattedTime,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (checkBtPermission(context)){
            if (bluetoothAdapter.isEnabled){
                val connectedDevices = bluetoothAdapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener{
                    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                        if (profile == BluetoothProfile.HEADSET){
                            val devices = proxy.connectedDevices
                            for (device in devices){
                                if (checkBtPermission(context)){
                                    val deviceName = device.name
//                                    binding.deviceName.text = deviceName
                                    Toast.makeText(context, deviceName, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        bluetoothAdapter.closeProfileProxy(profile, proxy)
                    }

                    override fun onServiceDisconnected(profile: Int) {
//                        binding.deviceName.text = "Disconnected"
                        Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()

                    }
                }, BluetoothProfile.HEADSET)


                LaunchedEffect(connectedDevices) {
                    bluetoothViewModel.bluetoothName.value?.let { name ->
                        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
                        // Lakukan hal-hal lain yang perlu Anda lakukan dengan 'name'
                    }
                }
//                if (connectedDevices){
//                    bluetoothViewModel.bluetoothName.observe(viewLifecycleOwner, Observer {
//                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
////                        binding.deviceName.text = it
//
//                    })
//                } else {
//                    Toast.makeText(context, "No Device Connected", Toast.LENGTH_SHORT).show()
//
////                    binding.deviceName.text = "No Device Connected"
//                }
            } else {
//                binding.deviceName.text = "Bluetooth is not enabled"
                Toast.makeText(context, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()

            }
        }

        Row(Modifier.fillMaxWidth()) {
            Button(onClick = {
                val socket = BluetoothSocketHolder.getBluetoothSocket()

                if (socket != null){
                    myThreadConnected = ThreadConnected(socket, true, context, title)
                    myThreadConnected!!.start()
                    Log.e("newBT", "start Thread connected")
                    Toast.makeText(context, "start Thread connected", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("newBT", "Connect to  your device first!")
                    Toast.makeText(context, "Connect to  your device first!", Toast.LENGTH_LONG).show()
                }
            }) {

            }
            Button(
                onClick = {
                        if (title != "") {

                            getPairedDevices(bluetooth, context, listPaired, listPairedBluetoothDevices)
                            isDialogVisible.value = true

                        } else isErrorTitle = true
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(0.5f)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF72D99D)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = primary
                )
            }
            /*stop*/
            Button(
                onClick = {
//                    connection.disconnect()

                    stopWatch.pause()
                    isStopwatch.value = false
                    isRecording.value = false

                    showResult = true

//                    myThreadConnected?.cancel()
//                    stopWatch.reset()

                },
                modifier = Modifier
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(0.5f)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF72D99D)
                )
            ) {

                Icon(
                    imageVector = Icons.Filled.StopCircle,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = primary
                )
            }
        }
        val socket = BluetoothSocketHolder.getBluetoothSocket()

        if (isDialogVisible.value) {
            DeviceSelectionDialog(
                listPaired = listPaired,
                listPairedBluetoothDevices = listPairedBluetoothDevices,
                onDeviceSelected = { deviceAddress ->
                    if (connection.connect(
                            deviceAddress,
                            true,
                            connectionListener,
                            receiveListener
                        )
                    ) {
                        Log.e("newBT", "Start connection process")
                        Toast.makeText(context, "Start connection process", Toast.LENGTH_SHORT).show()

                    } else {
                        Log.e("newBT","Start connection process failed")
                        Toast.makeText(context, "Start connection process failed", Toast.LENGTH_SHORT).show()
                    }
                },
                onDismiss = { isDialogVisible.value = false },
                isDialogVisible = isDialogVisible,
                isConnect = isConnect,
                stopWatch,
                isStopwatch,
                isRecording,
                context,
                bluetoothViewModel,
                title,
                socket

            )
        }

        if (showResult) {
            Spacer(modifier = Modifier.height(16.dp))
            val date = getCurrentTime()
            Text(text = "Recorded result", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Filename       : $title")
            Text(text = "Last modified  : $date")
            Text(text = "Duration       : ${stopWatch.formattedTime}")
            Spacer(modifier = Modifier.height(16.dp))

            val fm = "Record17Oct.wav"
            Button(
                onClick = {
                    navController.navigate("detail/$fm/$date")

                }, colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = Color.White
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Lihat detail", fontWeight = FontWeight.Bold)
                Icon(
                    painter = painterResource(id = R.drawable.ic_next_arrow),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        ModalBottomSheetM3(context)
    }
}

@Composable
private fun DeviceSelectionDialog(
    listPaired: MutableList<String?>,
    listPairedBluetoothDevices: MutableList<BluetoothDevice?>,
    onDeviceSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    isDialogVisible: MutableState<Boolean>,
    isConnect: MutableState<Boolean>,
    stopWatch: StopWatch, // Parameter stopWatch ditambahkan di sini
    isStopwatch: MutableState<Boolean>, // Parameter isStopwatch ditambahkan di sini
    isRecording: MutableState<Boolean>, // Parameter isRecording ditambahkan di sini
    context: Context,
    bluetoothViewModel: BluetoothViewModel,
    title: String,
    socket: BluetoothSocket?
) {
    var myThreadConnected: ThreadConnected? = null
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
//                    val device =
//                        pairedDevice?.split("\n".toRegex())!!.dropLastWhile { it.isEmpty() }
//                            .toTypedArray()
//                    val deviceBt = listPairedBluetoothDevices.toTypedArray()
//                    Log.e("device", device.toString())
//                    Log.e("device modr", "$device -- ${device[0]} -- ${device[1]}")

                    ListItem(
                        modifier = Modifier.clickable {
//                            val position = listDetectDevicesString.indexOf(it.name)
//                            if (bluetooth.requestPairDevice(/*listDetectBluetoothDevices[position]*/it)) {
////                                Log.d(BluetoothActivity.TAG, "Pair request send successfully $position")
//                                Toast.makeText(
//                                    context,
//                                    "Pair request send successfully",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }

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
                            Toast.makeText(context, "Connecting to ${device?.name}..", Toast.LENGTH_SHORT).show()
                            threadConnectBTDevice = ThreadConnectBTDevice(device!!, context)
                            threadConnectBTDevice.start()
                            Log.e("newBT thread connect", "onClick: $threadConnectBTDevice")
                            bluetoothViewModel.setBluetoothName(device.name)


//                            if (socket != null){
//                                myThreadConnected = ThreadConnected(socket, true, context, title)
//                                myThreadConnected!!.start()
//                                Log.e("newBT", "start Thread connected")
//                                Toast.makeText(context, "start Thread connected", Toast.LENGTH_LONG).show()
//                            } else {
//                                Log.e("newBT", "Connect to  your device first!")
//                                Toast.makeText(context, "Connect to  your device first!", Toast.LENGTH_LONG).show()
//                            }

                            isConnect.value = true
                            if (isConnect.value) {
                                stopWatch.start()
                                isStopwatch.value = true
                                isRecording.value = true
                            }
                            onDismiss()
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

fun logMsg(msg: String) {
    Log.d("connection receive", msg)
}