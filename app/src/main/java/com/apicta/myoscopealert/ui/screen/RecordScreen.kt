package com.apicta.myoscopealert.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.bt.BluetoothReceiver
import com.apicta.myoscopealert.ui.screen.common.ProcessWavFileData
import com.apicta.myoscopealert.ui.screen.common.SetUpChart
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.bt.BluetoothViewModel
import com.apicta.myoscopealert.ui.viewmodel.StopWatch
import com.apicta.myoscopealert.bt.BluetoothSocketHolder
import com.apicta.myoscopealert.bt.ThreadConnectBTDevice
import com.apicta.myoscopealert.bt.ThreadConnected
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.ui.screen.common.ChipCustom
import com.apicta.myoscopealert.utils.checkBtPermission
import com.apicta.myoscopealert.utils.getCurrentTime
import com.psp.bluetoothlibrary.Bluetooth
import com.psp.bluetoothlibrary.Connection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

//@RequiresApi(Build.VERSION_CODES.S)
//@Composable
//fun RecordScreen(navController: NavHostController, modifier: Modifier = Modifier) {
//    val bluetoothViewModel: BluetoothViewModel = viewModel()
//    var myThreadConnected: ThreadConnected? = null
////    var timeCode by Delegates.notNull<Long>()
//
//    val context = LocalContext.current
//    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.recording))
//
//    var title by rememberSaveable { mutableStateOf("record")}
//    var formatedTitle by remember {
//        mutableStateOf(title)
//    }
//    var isErrorTitle by rememberSaveable { mutableStateOf(false)}
//
//    var showResult by remember { mutableStateOf(false) }
//    val isRecording = remember { mutableStateOf(false) }
//    val isStopwatch = remember { mutableStateOf(false) }
//    val stopWatch = remember { StopWatch() }
//
//    // Connection object
//    val connection by remember { mutableStateOf(Connection(context)) }
//    val your_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//    connection.setUUID(your_uuid);
//    val isConnect = remember {
//        mutableStateOf(false)
//    }
//    val appContext = context.getActivity()
//    val bluetooth: Bluetooth = Bluetooth(context)
//    var isLoad by remember {
//        mutableStateOf(false)
//    }
//    var isButtonEnabled by remember { mutableStateOf(false) }
//
//    @Suppress("DEPRECATION") val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//
//    if (checkBtPermission(context)){
//        if (bluetoothAdapter.isEnabled){
//            val connectedDevices = bluetoothAdapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener{
//                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
//                    if (profile == BluetoothProfile.HEADSET){
//                        val devices = proxy.connectedDevices
//                        for (device in devices){
//                            if (devices.isNotEmpty()) {
//                                val device = devices.first()
//                                if (ActivityCompat.checkSelfPermission(
//                                        context,
//                                        Manifest.permission.BLUETOOTH_CONNECT
//                                    ) != PackageManager.PERMISSION_GRANTED
//                                ) {
//                                    // TODO: Consider calling
//                                    //    ActivityCompat#requestPermissions
//                                    // here to request the missing permissions, and then overriding
//                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                    //                                          int[] grantResults)
//                                    // to handle the case where the user grants the permission. See the documentation
//                                    // for ActivityCompat#requestPermissions for more details.
//                                    return
//                                }
//                                Toast.makeText(context, "Connected to ${device.name}", Toast.LENGTH_SHORT).show()
//                            } else {
//                                Toast.makeText(context, "No devices connected", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                    bluetoothAdapter.closeProfileProxy(profile, proxy)
//                }
//
//                override fun onServiceDisconnected(profile: Int) {
////                        binding.deviceName.text = "Disconnected"
//
//                    Toast.makeText(context, "Stetoscope not connected", Toast.LENGTH_SHORT).show()
//
//
//                }
//            }, BluetoothProfile.HEADSET)
//
//
//            LaunchedEffect(connectedDevices) {
//                bluetoothViewModel.bluetoothName.value?.let { name ->
//                    Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
//                    // Lakukan hal-hal lain yang perlu Anda lakukan dengan 'name'
//                }
//            }
//        } else {
//            // Check if not null
//            appContext?.let {
//                bluetooth.turnOnWithPermission(it)
//                Log.e("turn on bt", "call func")
//
//
//            }
//            Log.e("newBT check permission", "Bluetooth is not enabled")
//        }
//    }
//
//    val scope = rememberCoroutineScope()
//
//    Column(
//        modifier
//            .fillMaxSize()
//            .padding(horizontal = 16.dp)
//            .padding(top = 16.dp)
//            .verticalScroll(rememberScrollState())
//    ) {
//        TextField(
//            value = title,
//            onValueChange = { title = it
//                if (title!="") isErrorTitle = false
//                            },
//            placeholder = {
//                Text(
//                    text = "Type Filename",
//                    color = Color.LightGray,
//                    fontWeight = FontWeight.ExtraBold,
//                    fontFamily = poppins,
//                    fontSize = 24.sp,
//                    textAlign = TextAlign.Center
//                )
//            },
//            modifier = modifier
//                .background(color = Color.Transparent)
//                .align(Alignment.CenterHorizontally),
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = Color.Transparent,
//                unfocusedContainerColor = Color.Transparent,
//                disabledContainerColor = Color.Transparent,
//            ),
//            textStyle = TextStyle(
//                fontSize = 24.sp,
//                fontFamily = poppins,
//            ),
//            singleLine = true,
//            isError = isErrorTitle,
//            supportingText = {
//                if (isErrorTitle) {
//                    Text(
//                        modifier = modifier.fillMaxWidth(),
//                        text = "Please fill the title name",
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//            },
//            trailingIcon = {
//                if (isErrorTitle)
//                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
//            },
//            keyboardActions = KeyboardActions { isErrorTitle = title.isNotEmpty() }
//        )
//
//        Spacer(modifier = modifier.height(32.dp))
//
//
////        if (showResult) {
////            SetUpChart(ctx = context)
////        } else {
////            SetUpChart(context)
////        }
//        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
//        val filePath = "${musicDir.absolutePath}/1700032300811-defaultnamefff.wav"
//        if (/*isConnect.value && !showResult*/isRecording.value && isStopwatch.value) {
//            Column {
//                LottieAnimation(
//                    modifier = modifier
//                        .fillMaxHeight(0.4f)
//                        .fillMaxWidth(),
//                    composition = composition,
//                    iterations = LottieConstants.IterateForever,
//                )
//            }
//        } else if (showResult && isButtonEnabled) {
////            val filePath = "${musicDir.absolutePath}/$formatedTitle"
//            val files = musicDir.listFiles()
//            val wavFiles = files?.filter { it.extension == "wav" }
//            val sortedWavFiles = wavFiles?.sortedWith(compareBy { it.lastModified() })?.takeIf { it.isNotEmpty() }?.last()
//            val filePath = "${musicDir.absolutePath}/${sortedWavFiles?.name}"
//            Log.e("filepath", filePath)
//
//            if (sortedWavFiles!!.exists()) {
////                ProcessWavFileData2(filePath, context)
//                ProcessWavFileData(filePath, context, false)
//
//            } else {
//                SetUpChart(ctx = context)
//            }
//        } else if (isLoad && !isButtonEnabled) {
//            CircularProgressIndicator(modifier = modifier
//                .align(Alignment.CenterHorizontally)
//                .padding(top = 24.dp))
//        } else {
//            SetUpChart(context)
//
//        }
//
//        Spacer(modifier = modifier.height(16.dp))
//
////        STOPWATCH
//        Text(
//            text = stopWatch.formattedTime,
//            fontWeight = FontWeight.Bold,
//            fontSize = 48.sp,
//            color = Color.Black,
//            textAlign = TextAlign.Center,
//            modifier = modifier
//                .align(Alignment.CenterHorizontally)
//                .padding(bottom = 16.dp)
//        )
//
//        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//
//            if (!isRecording.value){
//
//                IconButton(
//                    onClick = {
//                        stopWatch.reset()
//                        if (title.isNotEmpty()) {
//                            val socket = BluetoothSocketHolder.getBluetoothSocket()
//
//                            if (socket != null){
//                                val currentTimeMillis = System.currentTimeMillis()
//                                val date = Date(currentTimeMillis)
//
//                                // Format tanggal ke yyyyMMddhhmmss
//                                val formatter = SimpleDateFormat("yyyyMMddHHmmss") // gunakan HH untuk 24 jam format
//                                val formattedDate = formatter.format(date)
//                                formatedTitle = "M-$formattedDate-$title.wav"
//                                Log.e("filename0", formatedTitle)
//
//                                myThreadConnected = ThreadConnected(socket, true, context, formatedTitle)
//                                myThreadConnected!!.start()
//
//
//                                isConnect.value = true
//                                stopWatch.start()
//                                isRecording.value = true
//                                isStopwatch.value = true
//                                Log.e("newBT", "Start Record Heartbeat")
//                                Toast.makeText(context, "Start Record Heartbeat", Toast.LENGTH_LONG).show()
//                            } else {
//                                Log.e("newBT", "Connect to  your device first!")
//                                Toast.makeText(context, "Connect to  your device first!", Toast.LENGTH_LONG).show()
//
//                            }
//                        } else isErrorTitle = true
//                    },
//                    modifier = modifier
//                        .size(86.dp)
//                        .clip(shape = CircleShape),
//                    colors = IconButtonDefaults.iconButtonColors(
//                        containerColor = Color(0xFF72D99D)
//                    )
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Mic,
//                        contentDescription = null,
//                        modifier = modifier
//                            .size(48.dp),
//                        tint = primary
//                    )
//                }
//            } else {
//                /*stop*/
//
//                IconButton(
//                    onClick = {
//                        // Pauses stopwatch and recording flags
//                        stopWatch.pause()
//                        isStopwatch.value = false
//                        isRecording.value = false
//
//                        // Initiates loading indication and delays to show results
//                        scope.launch {
//                            isLoad = true
//                            myThreadConnected?.cancel() // Safely stop the Bluetooth thread
//
//                            delay(2000) // Delay to ensure data processing completes
//                            showResult = true
//                            isLoad = false
//                        }
//
//                    },
//                    modifier = modifier
//                        .size(86.dp)
//                        .clip(shape = CircleShape),
//                    colors = IconButtonDefaults.iconButtonColors(
//                        containerColor = Color(0xFF72D99D)
//                    )
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.StopCircle,
//                        contentDescription = null,
//                        modifier = modifier
//                            .size(48.dp),
//                        tint = primary
//                    )
//                }
//            }
//        }
//
//        if (showResult) {
//            Log.e("newBT showresult", formatedTitle.toString())
//            Spacer(modifier = modifier.height(16.dp))
//            val date = getCurrentTime()
//            Text(text = "Recorded result", fontWeight = FontWeight.Bold, fontSize = 20.sp)
//            Text(text = "Filename       : $formatedTitle")
//            Text(text = "Last modified  : $date", fontSize = 13.sp)
//            Text(text = "Duration       : ${stopWatch.formattedTime}")
//            Spacer(modifier = modifier.height(16.dp))
//
//            val textLoad = if (isButtonEnabled) "Show Detail" else "Processing audio..."
//
//            LaunchedEffect(Unit) {
//                delay((15000)/*.random().toLong()*/)
//                isButtonEnabled = true
//            }
//            Button(
//                enabled = isButtonEnabled,
//                onClick = {
////                    val fm = "$title-${System.currentTimeMillis()}.wav"
//                    // Delay for 4 to 8 seconds (4000 to 8000 milliseconds)
//                    Log.e("filename2", formatedTitle)
//                    navController.navigate("detail/$formatedTitle/-1")
//
//                }, colors = ButtonDefaults.buttonColors(
//                    containerColor = primary,
//                    contentColor = Color.White
//                ),
//                modifier = modifier.align(Alignment.CenterHorizontally)
//            ) {
//                Text(text = /*"Lihat detail"*/textLoad, fontWeight = FontWeight.Bold)
//                if (isButtonEnabled) {
//
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_next_arrow),
//                        contentDescription = null,
//                        modifier = modifier.size(24.dp)
//                    )
//                } else {
//                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
//                }
//            }
//
//
//        } else if (isLoad) {
//            CircularProgressIndicator(modifier = modifier
//                .align(Alignment.CenterHorizontally)
//                .padding(top = 56.dp))
//        }
//        Spacer(modifier = modifier.weight(1f))
//        ModalBottomSheetM3(context, isConnect, stopWatch, isStopwatch, isRecording, bluetoothViewModel)
//
//    }
//}


@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RecordScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    bluetoothViewModel: BluetoothViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val hasShownNoDeviceSnackbar = remember { mutableStateOf(false) }

//    val bluetoothViewModel: BluetoothViewModel = viewModel()
//    var myThreadConnected: ThreadConnected? = null
    val myThreadConnectedState = remember { mutableStateOf<ThreadConnected?>(null) }


    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.recording))

    var title by rememberSaveable { mutableStateOf("record") }
    var formatedTitle by remember { mutableStateOf(title) }
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    val isRecording = remember { mutableStateOf(false) }


    // Kode inisialisasi koneksi dan Bluetooth (sesuai dengan kode asli Anda)
    val connection by remember { mutableStateOf(Connection(context)) }
    val your_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    connection.setUUID(your_uuid)
//    val isConnect = remember { mutableStateOf(false) }
    val isConnect by bluetoothViewModel.isConnect.collectAsState()
    val connectedDeviceName by bluetoothViewModel.bluetoothName.observeAsState("")
    Log.e("RecordScreen", "Connected to: $connectedDeviceName")
    Log.e("RecordScreen", "Connected status: $isConnect")
    val appContext = context.getActivity()
    val bluetooth: Bluetooth = Bluetooth(context)
    var isLoad by remember { mutableStateOf(false) }
    var isButtonEnabled by remember { mutableStateOf(false) }


    val isStopwatch = remember { mutableStateOf(false) }
    // Ingat hanya satu instance StopWatch
    val stopWatch = remember { StopWatch() }



    @Suppress("DEPRECATION") val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    val deviceToConnect: BluetoothDevice? = bluetoothAdapter.bondedDevices.firstOrNull()

    // Pengecekan dan koneksi ulang otomatis
    LaunchedEffect(deviceToConnect) {
        // Pastikan deviceToConnect tidak null dan belum terkoneksi
        if (deviceToConnect != null && !bluetoothViewModel.isConnect.value) {
            // Jika Anda memiliki fungsi koneksi khusus, panggil di sini.
            // Contoh: menggunakan ThreadConnectBTDevice (sesuaikan dengan implementasi Anda)
            ThreadConnectBTDevice(deviceToConnect, context) { deviceName ->
                bluetoothViewModel.setBluetoothName(deviceName)
            }.start()            // Anda bisa juga menampilkan snackbar untuk informasi
            scope.launch {
                snackbarHostState.showSnackbar("Menghubungkan ke ${deviceToConnect.name}")
            }
        }
    }

    DisposableEffect(Unit) {
        val receiver = BluetoothReceiver(bluetoothViewModel)
        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        context.registerReceiver(receiver, intentFilter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    if (checkBtPermission(context)) {
        if (bluetoothAdapter.isEnabled) {
            val connectedDevices = bluetoothAdapter.getProfileProxy(
                context,
                object : BluetoothProfile.ServiceListener {
                    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                        if (profile == BluetoothProfile.HEADSET) {
                            val devices = proxy.connectedDevices
                            if (devices.isNotEmpty()) {
                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.BLUETOOTH_CONNECT
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    return
                                }
//                                connectedDeviceName.value =
//                                    devices.first().name // Simpan nama perangkat pertama yang terhubung
                            } else if (!hasShownNoDeviceSnackbar.value) {
//                            scope.launch {
//                                snackbarHostState.showSnackbar("No devices connected")
//                            }
                                hasShownNoDeviceSnackbar.value = true
                            }
                        }
                        bluetoothAdapter.closeProfileProxy(profile, proxy)
                    }

                    override fun onServiceDisconnected(profile: Int) {
                        scope.launch {
//                        snackbarHostState.showSnackbar("Stethoscope not connected")
                        }
//                        connectedDeviceName.value = "" // Reset jika terputus
                    }
                },
                BluetoothProfile.HEADSET
            )
        } else {
            appContext?.let {
                bluetooth.turnOnWithPermission(it)
                Log.e("turn on bt", "call func")
            }
            Log.e("newBT check permission", "Bluetooth is not enabled")
//            scope.launch {
//                snackbarHostState.showSnackbar("Bluetooth is not enabled")
//            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Column(
            modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (!showResult) {

                TextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (title != "") isErrorTitle = false
                    },
                    placeholder = {
                        Text(
                            text = "Type Filename",
                            color = Color.LightGray,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = poppins,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = modifier
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
                                modifier = modifier.fillMaxWidth(),
                                text = "Please fill the title name",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        if (isErrorTitle)
                            Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                    },
                    keyboardActions = KeyboardActions { isErrorTitle = title.isNotEmpty() }
                )
            }

            Spacer(modifier = modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ChipCustom(
                    label = when {
                        isRecording.value -> "Recording..."
//                        isConnect.value -> "Device ${connectedDeviceName.value} connected"
                        isConnect -> "Device $connectedDeviceName connected"
                        else -> "Device not connected"
                    },
                    color = when {
                        isRecording.value -> Color.Blue
                        isConnect -> Color.Green
                        else -> Color.Red
                    }
                )
            }

            val musicDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            val filePath = "${musicDir.absolutePath}/1700032300811-defaultnamefff.wav"
            if (/*isConnect.value && !showResult*/isRecording.value && isStopwatch.value) {
                Column {
                    LottieAnimation(
                        modifier = modifier
                            .fillMaxHeight(0.4f)
                            .fillMaxWidth(),
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                    )
                }
            } else if (showResult && isButtonEnabled) {
//            val filePath = "${musicDir.absolutePath}/$formatedTitle"
                val files = musicDir.listFiles()
                val wavFiles = files?.filter { it.extension == "wav" }
                val sortedWavFiles = wavFiles?.sortedWith(compareBy { it.lastModified() })
                    ?.takeIf { it.isNotEmpty() }?.last()
                val filePath = "${musicDir.absolutePath}/${sortedWavFiles?.name}"
                Log.e("filepath", filePath)

                if (sortedWavFiles!!.exists()) {
//                ProcessWavFileData2(filePath, context)
                    ProcessWavFileData(filePath, context, false)

                } else {
                    SetUpChart(ctx = context)
                }
            } else if (isLoad && !isButtonEnabled) {
                CircularProgressIndicator(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp)
                )
            } else {
                SetUpChart(context)

            }

            Spacer(modifier = modifier.height(16.dp))

//        STOPWATCH
            Text(
                text = stopWatch.formattedTime,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )

            LaunchedEffect(isButtonEnabled) {
//                Log.d("DEBUG", "Recomposing, isButtonEnabled = $isButtonEnabled")
            }
            Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if (!isRecording.value && !showResult) {
                    IconButton(
                        onClick = {
                            stopWatch.reset()
                            if (title.isNotEmpty()) {
                                val socket = BluetoothSocketHolder.getBluetoothSocket()
                                if (socket != null) {
//                                    isConnect.value = true
                                    val currentTimeMillis = System.currentTimeMillis()
                                    val date = Date(currentTimeMillis)
                                    val formatter = SimpleDateFormat("yyyyMMddHHmmss")
                                    val formattedDate = formatter.format(date)
                                    formatedTitle = "M-$formattedDate-$title.wav"
                                    Log.e("RecordScreen", "Filename: $formatedTitle")

                                    myThreadConnectedState.value =
                                        ThreadConnected(
                                            socket,
                                            true,
                                            context,
                                            formatedTitle,
                                            bluetoothViewModel,
                                            onProcessingFinished = {
                                                // Callback: proses di ThreadConnected telah selesai
                                                isButtonEnabled = true
                                            }
                                        )
                                    myThreadConnectedState.value!!.start()
                                    stopWatch.start()
                                    isRecording.value = true
                                    isStopwatch.value = true
                                    Log.e("RecordScreen", "Start Record Heartbeat")

                                    scope.launch {
                                        snackbarHostState.showSnackbar("Start Record Heartbeat")
                                    }
                                } else {
                                    Log.e("RecordScreen", "Connect to your device first!")

                                    scope.launch {
                                        snackbarHostState.showSnackbar("Connect to your device first!")
                                    }
                                }
                            } else {
                                isErrorTitle = true
                            }
                        },
                        modifier = modifier
                            .size(86.dp)
                            .clip(shape = CircleShape),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(
                                0xFF72D99D
                            )
                        ),
                        enabled = isConnect
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = null,
                            modifier = modifier.size(48.dp),
                            tint = primary
                        )
                    }
                } else if (isRecording.value) {
                    IconButton(
                        onClick = {
                            stopWatch.pause()
                            isStopwatch.value = false
                            isRecording.value = false

                            scope.launch {
                                isLoad = true
                                myThreadConnectedState.value?.sendStopCommand()
//                                delay(500)
                                myThreadConnectedState.value?.cancel()
                                delay(2000)
                                showResult = true
                                isLoad = false
                            }
                        },
                        modifier = modifier
                            .size(86.dp)
                            .clip(shape = CircleShape),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.StopCircle,
                            contentDescription = null,
                            modifier = modifier.size(48.dp),
                            tint = primary
                        )
                    }
                } else {
                    Button(
                        onClick = { navController.navigate(BottomBarScreen.Record.route)
                                  if (!isButtonEnabled) {
                                      Toast.makeText(context, "Please wait until the process is complete", Toast.LENGTH_SHORT).show()
                                  }
                                  },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xBFFFC107), contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.Black),
                        shape = RoundedCornerShape(16.dp),
                        enabled = isButtonEnabled
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(
                                    imageVector = Icons.Filled.RestartAlt,
                                    contentDescription = null
                                )
                                Text("Mau rekam lagi?")
                            }
                        }
                }
            }

            if (showResult) {
                Log.e("newBT showresult", formatedTitle.toString())
                Spacer(modifier = modifier.height(16.dp))
                val date = getCurrentTime()
                Text(text = "Recorded result", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = "Filename       : $formatedTitle")
                Text(text = "Last modified  : $date", fontSize = 13.sp)
                Text(text = "Duration       : ${stopWatch.formattedTime}")
                Spacer(modifier = modifier.height(16.dp))

                val textLoad = if (isButtonEnabled) "Show Detail" else "Processing audio..."

                Button(
                    enabled = isButtonEnabled,
                    onClick = {
//                    val fm = "$title-${System.currentTimeMillis()}.wav"
                        // Delay for 4 to 8 seconds (4000 to 8000 milliseconds)
                        Log.e("filename2", formatedTitle)
                        navController.navigate("detail/$formatedTitle/-1")

                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = primary,
                        contentColor = Color.White
                    ),
                    modifier = modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = /*"Lihat detail"*/textLoad, fontWeight = FontWeight.Bold)
                    if (isButtonEnabled) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_next_arrow),
                            contentDescription = null,
                            modifier = modifier.size(24.dp)
                        )
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }


            } else if (isLoad) {
                CircularProgressIndicator(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 56.dp)
                )
            }
            Spacer(modifier = modifier.weight(1f))
            ModalBottomSheetM3(
                context,
                stopWatch,
                isStopwatch,
                isRecording,
                bluetoothViewModel,
                showResult
            )

        }

    }
    // Set callback onStopRecording pada instance yang sama
    LaunchedEffect(Unit) {
        stopWatch.onStopRecording = {

            stopWatch.pause()
            isStopwatch.value = false
            isRecording.value = false
            isLoad = true

            scope.launch {
                myThreadConnectedState.value?.sendStopCommand() // Send stop command
                delay(500) // Wait for stop command to be sent
                Log.e("RecordScreen", "Stop command sent, myyThreadConnected: ${myThreadConnectedState.value}")
                myThreadConnectedState.value?.cancel() // Cancel the thread
                delay(2000) // Wait for thread cancellation to complete
                Log.e("RecordScreen", "Thread cancelled")

                showResult = true // Show the result after stopping the recording
                isButtonEnabled = true
            }
            isLoad = false

        }
    }

}


