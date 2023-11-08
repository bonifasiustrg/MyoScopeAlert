package com.apicta.myoscopealert.ui.screen

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.viewmodel.StopWatch
import com.psp.bluetoothlibrary.Bluetooth
import com.psp.bluetoothlibrary.BluetoothListener
import com.psp.bluetoothlibrary.Connection
import kotlinx.coroutines.delay
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.time.ExperimentalTime

interface OnConnectionListener {
    fun onConnectionStateChanged(socket: BluetoothSocket?, state: Int)
    fun onConnectionFailed(errorCode: Int)
}

@OptIn(ExperimentalTime::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RecordScreen(navController: NavHostController) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.recording))
    var title by remember {
        mutableStateOf("")
    }

    var isRecording by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isStopwatch = remember {
        mutableStateOf(false)
    }
    val filePath =
        "/storage/emulated/0/Android/data/com.apicta.myoscopealert/files/Recordings/Record17Oct.wav"

//    var receivedDataList = remember { mutableStateOf(emptyList<Int>()) }
    var receivedDataList by remember { mutableStateOf(mutableListOf<Int>()) }

    var isDialogVisible = remember { mutableStateOf(false) }


    // Connection object
    val connection by remember { mutableStateOf(Connection(context)) }
    val your_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    connection.setUUID(your_uuid);
    DisposableEffect(Unit) {
        onDispose {
            connection.disconnect()
        }
    }
    // Bluetooth object
    var bluetooth: Bluetooth? = null
    bluetooth = Bluetooth(context)
    val listPaired = remember { mutableListOf<String?>() }
//    //    val listPaired by rememberUpdatedState<List<String>>(emptyList())
//    var listPaired = remember { mutableStateOf(emptyList<String>()) }

    val listPairedBluetoothDevices = remember { mutableListOf<BluetoothDevice?>() }
//    val connectionListener = rememberUpdatedState(BluetoothListener.onConnectionListener {
//        // Implement your connection listener logic here
//        // You can access receivedDataList and update the UI accordingly
//    })
//
//    val receiveListener = rememberUpdatedState(BluetoothListener.onReceiveListener { receivedData ->
//        // Implement your receive listener logic here
//        // You can access receivedDataList and update the UI accordingly
//    })
// Permission state
//    val permissionState = rememberPermissionState(android.Manifest.permission.BLUETOOTH_CONNECT)
//    val context = LocalContext.current
//    // Check and request Bluetooth permission
//    LaunchedEffect(permissionState) {
//        if (!permissionState.hasPermission) {
//            permissionState.launchPermissionRequest()
//        } else {
//            getPairedDevices()
//        }
//    }
    val connectionListener: BluetoothListener.onConnectionListener = object :
        BluetoothListener.onConnectionListener {
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onConnectionStateChanged(socket: BluetoothSocket?, state: Int) {
            when (state) {
                Connection.CONNECTING -> {
                    logMsg("Connecting...")
//                    txtDisplay!!.append("\n[ST] Connecting...")
//                    setDisplayMessageScrollBottom()
                }

                Connection.CONNECTED -> {
                    logMsg("Connected")
//                    txtDisplay!!.append("\n[ST] Connected")
//                    setDisplayMessageScrollBottom()
                }

                Connection.DISCONNECTED -> {
                    logMsg("Disconnected")
//                    txtDisplay!!.append("\n[ST] Disconnected")
//                    setDisplayMessageScrollBottom()
                    connection.disconnect()
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
                }

                Connection.CONNECT_FAILED -> {
                    logMsg("Connect Failed")
//                    txtDisplay!!.append("\n[ST] Connect failed")
//                    setDisplayMessageScrollBottom()
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

//            logMsg("[RX] $receivedData")
//            txtDisplay!!.append("\n[RX] $receivedData")
//            setDisplayMessageScrollBottom()


            // Menambahkan data yang diterima ke dalam ArrayList
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
        val stopWatch = remember { StopWatch() }
//        StopWatchDisplay(
//            formattedTime = stopWatch.formattedTime,
//            onStartClick = stopWatch::start,
//            onPauseClick = stopWatch::pause,
//            onResetClick = stopWatch::reset
//        )

        TextField(
            value = title,
            onValueChange = { title = it },
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
            )
        )

        Spacer(modifier = Modifier.height(32.dp))


        if (!isRecording) {
            if (showResult) {
//                ProcessWavFileData(filePath, context)
                SetUpChart(ctx = context)
            } else {
                SetUpChart(context)
            }
        } else {
            Column {

                LottieAnimation(
                    modifier = Modifier
                        .fillMaxHeight(0.4f)
                        .fillMaxWidth(),
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stopWatch.formattedTime,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
//        val currentTime = rememberUpdatedState(getCurrentTime())
//        val currentTimeNoCLock = rememberUpdatedState(getCurrentTimeNoClock())
//        Box(
//            modifier = Modifier
//                .padding(8.dp)
//                .clip(RoundedCornerShape(50.dp))
//                .background(secondary)
//                .padding(vertical = 14.dp, horizontal = 64.dp)
//                .align(Alignment.CenterHorizontally)
//        ) {
//
//            Text(
//                text = currentTimeNoCLock.value,
//                style = TextStyle(
//                    color = Color.Black,
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 16.sp
//                )
//            )
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//        Stopwatch(isStopwatch)



        val blStatus = rememberSaveable {
            mutableStateOf(false)
        }
        var lastTimer = "00:00:000"

        var showDeviceConnect = remember {
            mutableStateOf(false)
        }
        /*Connect BT Button*/
        Row(Modifier.fillMaxWidth()) {
            Button(
                onClick = {

//                    isStopwatch.value = true
//                    isRecording = true

//                        context.startActivity(Intent(context, ConnectActivity::class.java))
//                    stopWatch.start()

//                    getPairedDevices(listPaired, context)
                    // Get Paired devices list
                    getPairedDevices(bluetooth, context, listPaired, listPairedBluetoothDevices)

                    isDialogVisible.value = true

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

//                    stopWatch.pause()
//                    isStopwatch.value = false
//                    isRecording = false
//
//                    showResult = true


                    connection.disconnect()
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
                        Log.e("connect", "Start connection process")
                    } else {
                        Log.e("connect","Start connection process failed")
                    }
                },
                onDismiss = { isDialogVisible.value = false },
                isDialogVisible
            )
        }

        if (showResult) {
            Spacer(modifier = Modifier.height(16.dp))
            val date = getCurrentTime()
            Text(text = "Recorded result", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Filename       : $title")
            Text(text = "Last modified  : $date")
            Text(text = "Duration  : ${stopWatch.formattedTime}")
            Log.e("result", "title $title data $date duration $lastTimer")
            Spacer(modifier = Modifier.height(16.dp))




            val fm = "Record17Oct.wav"
            Button(
                onClick = {
                    navController.navigate("detail/$fm/30 Oktober 2023")

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Stopwatch(isStart: MutableState<Boolean>) {
    var elapsedTime by remember { mutableLongStateOf(0L) }
    val isRunning = isStart.value

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000) // Wait for 1 second
            elapsedTime++
        }
    }

    val hours = (elapsedTime / 3600).toString().padStart(2, '0')
    val minutes = ((elapsedTime % 3600) / 60).toString().padStart(2, '0')
    val seconds = (elapsedTime % 60).toString().padStart(2, '0')

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$hours:$minutes:$seconds",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}



//@Composable
//fun DeviceAddressAndConnect(
//    connection: Connection,
//    receivedDataList: List<Int>,
//    showDeviceConnect: MutableState<Boolean>
//) {
//    // Implement your device selection logic here
//    // You can use AlertDialog or other UI components to select a device
//    Log.e("connect", "${connection.isConnected} ${connection.connectTimeout}")
//    Log.e("connect", "$receivedDataList.")
//
//    AlertDialog(
//        onDismissRequest = {
//            // Dismiss the dialog when the user clicks outside the dialog or on the back
//            // button. If you want to disable that functionality, simply use an empty
//            // onCloseRequest.
//            showDeviceConnect.value = false
//        },
//        title = {
//            Text(text = "Dialog Title")
//        },
//        text = {
//            Text("Here is a text ")
//        },
//        confirmButton = {
//            Button(
//
//                onClick = {
//                    showDeviceConnect.value = false
//                }) {
//                Text("This is the Confirm Button")
//            }
//        },
//        dismissButton = {
//            Button(
//
//                onClick = {
//                    showDeviceConnect.value = false
//                }) {
//                Text("This is the dismiss Button")
//            }
//        }
//    )
//
//}
@Composable
private fun DeviceSelectionDialog(
    listPaired: MutableList<String?>,
    listPairedBluetoothDevices: MutableList<BluetoothDevice?>,
    onDeviceSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    isDialogVisible: MutableState<Boolean>
) {
    AlertDialog(onDismissRequest = {  /*isDialogVisible.value = false*/ }, confirmButton = { /*isDialogVisible.value = false*/ }, title = { Text(text = "Select device") },
        text = {
            Log.e("alert", listPaired.toString())
            Log.e("alert", listPairedBluetoothDevices.toString())
            LazyColumn {
                items(items = listPaired) { pairedDevice ->
                    val device = pairedDevice?.split("\n".toRegex())!!.dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    Log.e("device", device.toString())
                    Log.e("device modr", "$device -- ${device[0]} -- ${device[1]}")

//                    Text(
//                        text = device[0], // Display device name or other information
//                        modifier = Modifier
//                            .clickable {
//                                onDeviceSelected(device[1])
//                                onDismiss()
//                            }
//                            .padding(16.dp)
//                    )
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

                            onDeviceSelected(device[1])
                            onDismiss()
                        },
                        headlineContent = { Text(text = device[0]) },
                        leadingContent = {
                            Icon(imageVector = Icons.Default.SettingsRemote, contentDescription = null)
                        }
                    )
                }
            }
        },)

}


@Composable
fun Disconnect(
    connection: Connection,
    receivedDataList: List<Int>,
    context: Context
) {
    DisposableEffect(Unit) {
        onDispose {
            if (connection.isConnected()) {
                connection.disconnect()
                // Handle received data and save it to a file if needed
                // You can use receivedDataList and context to handle the received data
                // For example, saving the data to a file
            }
        }
    }
}


//@RequiresApi(Build.VERSION_CODES.S)
//private fun getPairedDevices(list: ArrayList<String>) {
//    // initialize bluetooth object
//    val bluetooth = Bluetooth(this)
//    val deviceList = bluetooth.getPairedDevices()
//    if (deviceList.size > 0) {
//        for (device in deviceList) {
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.BLUETOOTH_CONNECT
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
//                    BluetoothActivity.REQUEST_BLUETOOTH_PERMISSION
//                )
//                return
//            }
//            list.add(
//                """
//                        ${device.name}
//                        ${device.address}
//                        """.trimIndent()
//            )
//            Log.d("connect paired", "Paired device is " + device.name)
//        }
//    }
//}

// Get paired devices function
fun getPairedDevices(listPaired: MutableState<List<String>>, context: Context) {
    val bluetooth = Bluetooth(context)
    val deviceList = bluetooth.getPairedDevices()
    if (deviceList.isNotEmpty()) {
        listPaired.value = deviceList.map { if (ActivityCompat.checkSelfPermission(
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
            "${it.name}\n${it.address}" }
    }
}













@Composable
fun StopWatchDisplay(
    formattedTime: String,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formattedTime,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.Black
        )
        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onStartClick) {
                Text("Start")
            }
            Spacer(Modifier.width(16.dp))
            Button(onPauseClick) {
                Text("Pause")
            }
            Spacer(Modifier.width(16.dp))
            Button(onResetClick) {
                Text("Reset")
            }
        }
    }
}





/*FUNCTION*/
fun getCurrentTimeNoClock(): String {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    val currentTime = Date()
    return dateFormat.format(currentTime)
}
fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss", Locale.getDefault())
    val currentTime = Date()
    return dateFormat.format(currentTime)
}

fun logMsg(msg: String) {
    Log.d("connection receive", msg)
}