package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(navController: NavHostController) {
//    Text(text = "Record Screen")
    var title by remember {
        mutableStateOf("Record17Oct")
    }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var isBTConnected by rememberSaveable { mutableStateOf(false) }
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.recording))
    val context = LocalContext.current
    val isStopwatch = remember {
        mutableStateOf(false)
    }
    val filePath =
        "/storage/emulated/0/Android/data/com.apicta.myoscopealert/files/Recordings/Record17Oct.wav"
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

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
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,

                ),
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontFamily = poppins,
            )
        )

        Spacer(modifier = Modifier.height(32.dp))


        if (!isRecording) {
            if (showResult) {
                ProcessWavFileData(filePath, context)
//                SetUpChart(ctx = context)
            } else {
                SetUpChart(ctx)
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

        val currentTime = rememberUpdatedState(getCurrentTime())
        val currentTimeNoCLock = rememberUpdatedState(getCurrentTimeNoClock())
        Box(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(secondary)
                .padding(vertical = 14.dp, horizontal = 64.dp)
                .align(Alignment.CenterHorizontally)
        ) {

            Text(
                text = currentTimeNoCLock.value,
                style = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Stopwatch(isStopwatch)
        val blStatus = rememberSaveable {
            mutableStateOf(false)
        }
        Row(Modifier.fillMaxWidth()) {
            if (blStatus.value) {
                Button(
                    onClick = {
                        isRecording = !isRecording
                        isStopwatch.value = isRecording
                        if (isRecording == false) {
                            showResult = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(0.5f)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF72D99D)
                    )
                ) {

                    Icon(
                        imageVector = if (isRecording) Icons.Filled.StopCircle else Icons.Filled.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else if (isBTConnected == false) {
                Button(
                    onClick = {
                        if (!isBTConnected) {
                            navController.navigate("connect_bluetooth")
                        }
                        scope.launch {
                            delay(800)
                            isBTConnected = true
                            blStatus.value = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(0.5f)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xBFFFC107)
                    )
                ) {
                    Icon(
                        imageVector = if (isRecording) {
                            Icons.Filled.BluetoothDisabled

                        } else {
                            Icons.Filled.BluetoothConnected
                        },
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(text = "Bluetooth not connected", color = Color.Black)
                }
            }

//            Spacer(modifier = Modifier.width(16.dp))

        }

        if (showResult) {

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Recorded result", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Filename       : $title")
            Text(text = "Last modified  : ${currentTime.value}")
            Spacer(modifier = Modifier.height(16.dp))




            val fm = "Record17Oct.wav"
            Button(
                onClick = {
                    navController.navigate("detail/$fm/17 Oktober 2023")

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

//        Spacer(Modifier.height(16.dp))
//        Button(
//            onClick = { isRecording = false },
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(shape = RoundedCornerShape(0.5f)),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color(0xFFFF6F6F)
//            )
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_pause),
//                contentDescription = null,
//                modifier = Modifier
//                    .padding(6.dp)
//                    .size(24.dp)
//            )
//        }

    }

}
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


//        Row(
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Button(
//                onClick = {
//                    isRunning = !isRunning
//                },
//                modifier = Modifier.padding(8.dp)
//            ) {
//                Text(if (isRunning) "Pause" else "Start")
//            }

//            Button(
//                onClick = {
//                    // Reset the stopwatch
//                    elapsedTime = 0
//                },
//                modifier = Modifier.padding(8.dp)
//            ) {
//                Text("Reset")
//            }
//        }
    }
}


@Preview(showBackground = true)
@Composable
fun RecordScreenPrev() {
    RecordScreen(rememberNavController())
}