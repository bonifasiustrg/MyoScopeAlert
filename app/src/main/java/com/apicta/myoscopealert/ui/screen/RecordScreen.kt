package com.apicta.myoscopealert.ui.screen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.models.FileModel
import com.apicta.myoscopealert.ui.screen.common.ProcessWavFileData2
import com.apicta.myoscopealert.ui.screen.common.SetUpChart
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.viewmodel.BluetoothViewModel
import com.apicta.myoscopealert.ui.viewmodel.StopWatch
import com.apicta.myoscopealert.utils.BluetoothSocketHolder
import com.apicta.myoscopealert.utils.ThreadConnectBTDevice
import com.apicta.myoscopealert.utils.ThreadConnected
import com.apicta.myoscopealert.utils.checkBtPermission
import com.apicta.myoscopealert.utils.convertIntArrayToWav
import com.apicta.myoscopealert.utils.fileListDir
import com.apicta.myoscopealert.utils.getCurrentTime
import com.psp.bluetoothlibrary.Bluetooth
import com.psp.bluetoothlibrary.BluetoothListener
import com.psp.bluetoothlibrary.Connection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RecordScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val bluetoothViewModel: BluetoothViewModel = viewModel()
    var myThreadConnected: ThreadConnected? = null
    var timeCode by Delegates.notNull<Long>()

    val context = LocalContext.current
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.recording))

    var title by rememberSaveable { mutableStateOf("")}
    var formatedTitle by remember {
        mutableStateOf(title)
    }
    var isErrorTitle by rememberSaveable { mutableStateOf(false)}

    var showResult by remember { mutableStateOf(false) }
    val isRecording = remember { mutableStateOf(false) }
    val isStopwatch = remember { mutableStateOf(false) }
    val stopWatch = remember { StopWatch() }

    // Connection object
    val connection by remember { mutableStateOf(Connection(context)) }
    val your_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    connection.setUUID(your_uuid);
    val isConnect = remember {
        mutableStateOf(false)
    }
    val appContext = context.getActivity()
    val bluetooth: Bluetooth = Bluetooth(context)
    var isLoad by remember {
        mutableStateOf(false)
    }
    var isButtonEnabled by remember { mutableStateOf(false) }

    @Suppress("DEPRECATION") val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

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
        } else {
            // Check if not null
            appContext?.let {
                bluetooth.turnOnWithPermission(it)
                Log.e("turn on bt", "call func")
                // With user permission
            }
            Log.e("newBT check permission", "Bluetooth is not enabled")
        }
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier
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
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
            },
            keyboardActions = KeyboardActions { isErrorTitle = title.isNotEmpty() }
        )

        Spacer(modifier = modifier.height(32.dp))


//        if (showResult) {
//            SetUpChart(ctx = context)
//        } else {
//            SetUpChart(context)
//        }
        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
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
            val sortedWavFiles = wavFiles?.sortedWith(compareBy { it.lastModified() })?.last()
            val filePath = "${musicDir.absolutePath}/${sortedWavFiles?.name}"
            Log.e("filepath", filePath)

            if (sortedWavFiles!!.exists()) {
                ProcessWavFileData2(filePath, context)
            } else {
                SetUpChart(ctx = context)
            }
        } else if (isLoad && !isButtonEnabled) {
            CircularProgressIndicator(modifier = modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp))
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

        timeCode = System.currentTimeMillis()
        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (!isRecording.value){

                IconButton(
                    onClick = {
                        stopWatch.reset()
                        if (title != "") {
                            val socket = BluetoothSocketHolder.getBluetoothSocket()

                            if (socket != null){
                                formatedTitle = "$title-$timeCode.wav"
                                Log.e("filename0", formatedTitle)

                                myThreadConnected = ThreadConnected(socket, true, context, formatedTitle)
                                myThreadConnected!!.start()
                                isConnect.value = true
                                stopWatch.start()
                                isRecording.value = true
                                isStopwatch.value = true
                                Log.e("newBT", "Start Record Heartbeat")
                                Toast.makeText(context, "Start Record Heartbeat", Toast.LENGTH_LONG).show()
                            } else {
                                Log.e("newBT", "Connect to  your device first!")
                                Toast.makeText(context, "Connect to  your device first!", Toast.LENGTH_LONG).show()
                            }
                        } else isErrorTitle = true
                    },
                    modifier = modifier
                        .size(86.dp)
                        .clip(shape = CircleShape),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF72D99D)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = null,
                        modifier = modifier
                            .size(48.dp),
                        tint = primary
                    )
                }
            } else {
                /*stop*/

                IconButton(
                    onClick = {
//                    connection.disconnect()

                        stopWatch.pause()
                        isStopwatch.value = false
                        isRecording.value = false


                        scope.launch {
                            isLoad = true
                            myThreadConnected?.cancel()
                            delay(2000)
                            showResult = true
                            isLoad = false
                        }



                    },
                    modifier = modifier
                        .size(86.dp)
                        .clip(shape = CircleShape),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF72D99D)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.StopCircle,
                        contentDescription = null,
                        modifier = modifier
                            .size(48.dp),
                        tint = primary
                    )
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

            LaunchedEffect(Unit) {
                delay((25000)/*.random().toLong()*/)
                isButtonEnabled = true
            }
            Button(
                enabled = isButtonEnabled,
                onClick = {
//                    val fm = "$title-${System.currentTimeMillis()}.wav"
                    // Delay for 4 to 8 seconds (4000 to 8000 milliseconds)
                    Log.e("filename2", formatedTitle)
                    navController.navigate("detail/$formatedTitle/$date")

                }, colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = Color.White
                ),
                modifier = modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = /*"Lihat detail"*/textLoad, fontWeight = FontWeight.Bold)
                Icon(
                    painter = painterResource(id = R.drawable.ic_next_arrow),
                    contentDescription = null,
                    modifier = modifier.size(24.dp)
                )
            }


        } else if (isLoad) {
            CircularProgressIndicator(modifier = modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 56.dp))
        }
        Spacer(modifier = modifier.weight(1f))
        ModalBottomSheetM3(context, isConnect, stopWatch, isStopwatch, isRecording, bluetoothViewModel)
    }
}