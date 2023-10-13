package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(navController: NavHostController) {
//    Text(text = "Record Screen")
    var title by remember {
        mutableStateOf("")
    }
    var duration by remember {
        mutableStateOf(0)
    }
    
    var isRecording by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.heartbeat))
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TextField(
            value = title,
            onValueChange = { title = it },
            placeholder = {
                Text(
                    text = "Masukkan Nama File",
                    color = Color.Gray,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = poppins,
                    fontSize = 24.sp
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
            Image(
                painter = painterResource(id = R.drawable.chart),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        } else {
            LottieAnimation(
                modifier = Modifier.fillMaxHeight(0.4f),
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val currentTime = rememberUpdatedState(getCurrentTime())
        val currentTimeNoCLock = rememberUpdatedState(getCurrentTimeNoClock())
        Box(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(primary)
                .padding(vertical = 14.dp, horizontal = 64.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = currentTimeNoCLock.value,
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        val blStatus = remember {
            mutableStateOf(false)
        }
        Row(Modifier.fillMaxWidth()) {
            if (blStatus.value) {
                Button(
                    onClick = {
                        isRecording = !isRecording
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
                        imageVector = if (isRecording) Icons.Filled.StopCircle else Icons.Filled.Hearing,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                Button(
                    onClick = {
                        blStatus.value = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(0.5f)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xBFFFC107)
                    )
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Filled.BluetoothDisabled else Icons.Filled.BluetoothConnected,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(text = "Bluetooth not connected")
                }
            }

//            Spacer(modifier = Modifier.width(16.dp))

        }

        if (showResult) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Recorded result", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Filename       : $title")
            Text(text = "Last modified  : ${currentTime.value}")

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

@Preview(showBackground = true)
@Composable
fun RecordScreenPrev() {
    RecordScreen(rememberNavController())
}