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
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RecordScreen(navController: NavHostController) {
    val bluetoothViewModel: BluetoothViewModel = viewModel()
    var myThreadConnected: ThreadConnected? = null

    val context = LocalContext.current
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.recording))

    var title by rememberSaveable { mutableStateOf("defaultname")}
    var formatedTitle = title
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


        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (!isRecording.value){

                IconButton(
                    onClick = {
                        if (title != "") {
                            val socket = BluetoothSocketHolder.getBluetoothSocket()

                            if (socket != null){
                                formatedTitle = "$title-${System.currentTimeMillis()}.wav"
                                myThreadConnected = ThreadConnected(socket, true, context, formatedTitle)
                                myThreadConnected!!.start()
                                isConnect.value = true
                                stopWatch.start()
                                isRecording.value = true
                                isStopwatch.value = true
                                Log.e("newBT", "start Thread connected")
                                Toast.makeText(context, "start Thread connected", Toast.LENGTH_LONG).show()
                            } else {
                                Log.e("newBT", "Connect to  your device first!")
                                Toast.makeText(context, "Connect to  your device first!", Toast.LENGTH_LONG).show()
                            }
                        } else isErrorTitle = true
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(shape = CircleShape),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF72D99D)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = null,
                        modifier = Modifier
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
                            myThreadConnected?.cancel()
//                    stopWatch.reset()
                            delay(2000)
                            showResult = true
                        }



                    },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(shape = CircleShape),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF72D99D)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.StopCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp),
                        tint = primary
                    )
                }
            }
        }

        if (showResult) {
            val fileList = ArrayList<FileModel>()
            Log.e("newBT show", "$fileList")
            val ctx = LocalContext.current
            fileListDir(ctx, fileList)
            val filename: String? = fileList.firstOrNull()?.name
            Log.e("newBT showresult", filename.toString())
            Spacer(modifier = Modifier.height(16.dp))
            val date = getCurrentTime()
            Text(text = "Recorded result", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Filename       : $filename")
            Text(text = "Last modified  : $date")
            Text(text = "Duration       : ${stopWatch.formattedTime}")
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
//                    val fm = "$title-${System.currentTimeMillis()}.wav"
                    navController.navigate("detail/$filename/$date")

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
        ModalBottomSheetM3(context, isConnect, stopWatch, isStopwatch, isRecording, bluetoothViewModel)
    }
}