package com.apicta.myoscopealert.ui.screen


import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.databinding.SignalChartBinding
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.ui.screen.common.CardContent
import com.apicta.myoscopealert.ui.screen.common.ProcessWavFileData
import com.apicta.myoscopealert.ui.theme.cardsecondary
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.terniary
import com.apicta.myoscopealert.ui.viewmodel.DiagnosesViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException


@Composable
fun FileDetail(
    filename: String?,
    fileDate: String?,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: DiagnosesViewModel = hiltViewModel()
    val storedToken by viewModel.userToken.collectAsState()
    Log.e("usertoken", "$storedToken")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isBack by remember { mutableStateOf(false) }

//    val filePath = "/storage/emulated/0/Android/data/com.apicta.myoscopealert/files/Recordings/$filename"

    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    val filePath = "${musicDir.absolutePath}/$filename"
    Log.e("newBT save", "$filePath")
    var isPredicting by remember { mutableStateOf(false) }
    val isPlaying = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

//    val mediaPlayer = remember {
//        MediaPlayer().apply {
//            setDataSource(filePath)
//            prepare()
//        }
//    }
//    val animatedProgress = animateFloatAsState(
//        targetValue = progress,
//        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
//    ).value

    Column(
        modifier = modifier
            .fillMaxSize()
//            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {


        Column(
            modifier
                .fillMaxWidth()
                .background(primary)
                .padding(16.dp)
        ) {
            Spacer(modifier = modifier.height(16.dp))

            Text(
                text = "$filename",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(text = "Doctor         : Saparudin ", color = Color.White)
            Text(text = "Date           : $fileDate", color = Color.White)
        }
        Spacer(modifier = modifier.height(24.dp))

        Text(
            text = "Heartbeat Graph", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = poppins
        )
        Spacer(modifier = modifier.height(8.dp))

        var isZooming by remember {
            mutableStateOf(false)
        }
//         SetUpChart(context)
        ProcessWavFileData(filePath, context, isZooming)

        // Menampilkan progress bar
        IconButton(
//            shape = CircleShape,
            onClick = {
                isZooming = !isZooming
                Log.e("zoom", isZooming.toString())
            },
            modifier = modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = if (isZooming) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                contentDescription = null,
                modifier = modifier.size(24.dp)
            )
        }
//        LinearProgressIndicator(
//            progress = animatedProgress,
//            modifier = modifier
//                .fillMaxWidth()
//                .height(24.dp)
//                .padding(horizontal = 28.dp),
//            color = Color.Blue.copy(alpha = 0.2f)
//        )
//        Button(
//            shape = CircleShape,
//            onClick = {
//                // Mulai atau berhenti memutar audio
//                if (isPlaying.value) {
//                    if (mediaPlayer.isPlaying) {
//                        mediaPlayer.pause()
//                    }
//                } else {
//                    mediaPlayer.start()
//                    // Memantau progress audio
//                    CoroutineScope(Dispatchers.IO).launch {
//                        while (isPlaying.value) {
//                            val currentPosition = mediaPlayer.currentPosition.toFloat()
//                            val totalDuration = mediaPlayer.duration.toFloat()
//                            Log.e("progress", "$currentPosition | $totalDuration")
//                            val progressPercentage = currentPosition / totalDuration
//                            progress = progressPercentage
//                            if (progress == 1.0f) {
//                                isPlaying.value = false
//                            }
//                            if (isPlaying.value && mediaPlayer.currentPosition >= mediaPlayer.duration) {
//                                // Audio telah diputar sampai selesai
//                                isPlaying.value = false
//                                // Tampilkan pesan ke pengguna jika diperlukan
//                            }
//                            delay(200)
//
//                        }
//                    }
//                }
//                isPlaying.value = !isPlaying.value
//            },
//            modifier = modifier.padding(top = 16.dp)
//        ) {
//            Icon(
//                imageVector = if (isPlaying.value) Icons.Default.Stop else Icons.Default.PlayArrow,
//                contentDescription = null,
//                modifier = modifier.size(32.dp)
//            )
//        }
//        Spacer(modifier = modifier.height(4.dp))

        val isVerified by remember {
            mutableStateOf(true)
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = terniary
            ),
            modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            CardContent(if (filename != "Record17Oct.wav") isVerified else false)
        }

        Spacer(modifier = modifier.height(6.dp))
        Text(
            text = "Predict Your Heart Health",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = poppins

        )
        val predictResponse by viewModel.predictResponse.collectAsState()
//        LaunchedEffect(predictResponse) {
//            // Set isPredicting menjadi false saat nilai predictResponse berubah
//            isLoading.value == false
//        }

        if (predictResponse != null) {
            isLoading.value = false

            Box(
                modifier = modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        if (predictResponse!!.result == 0) Color(0xFF72D99D) else Color(
                            0xFFFF6F6F
                        )
                    )
                    .padding(vertical = 14.dp, horizontal = 64.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.8f)

            ) {
                Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (predictResponse!!.result == 0) "Normal Heart" else "Myocardial Infarction",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )

                    Icon(
                        imageVector = if (predictResponse!!.result == 0) Icons.Outlined.CheckCircle else Icons.Outlined.Close,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = modifier.height(16.dp))
        }
        if (isLoading.value) {
            CircularProgressIndicator(modifier = modifier.align(Alignment.CenterHorizontally))
        }


        Button(
//            enabled = !isPredicting,
            onClick = {
//                isPredicting = true
                if (isBack) {
                    navController.navigate(BottomBarScreen.History.route) {
                        // Pop up to the start destination of the graph
                        popUpTo(navController.graph.startDestinationId) {
                            // Pop all inclusive
                            inclusive = true
                        }
                        // Avoid multiple copies of the same destination when re-selecting it
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                } else if (!isPredicting) {
                    isLoading.value = true

                    isPredicting = true
                    /*FUNGSI PREDICT*/
                    val file = File(filePath)

                    // Periksa apakah file ada
                    if (file.exists()) {
                        scope.launch {
                            // Sekarang variabel 'body' adalah objek MultipartBody.Part yang dapat Anda gunakan untuk mengirim file dalam permintaan API.
//                            viewModel.performPrediction(/*token, */body)
                            viewModel.performPredict(filePath, storedToken!!)
//                            delay(1500)
//                            delay(2000)

                            Log.e(
                                "prediksi",
                                "Hasil Prediksi -> ${predictResponse?.result}"
                            )
                        }

                        Log.e("DiagnosesViewModel", "File sended at path: $filePath")
                        Toast.makeText(context, "Analyzing your heartbeat...", Toast.LENGTH_SHORT)
                            .show()
                        isBack = true
                        isPredicting = false
                    } else {
                        // Handle kesalahan jika file tidak ditemukan
                        Log.e("DiagnosesViewModel", "File not found at path: $filePath")
                        Toast.makeText(
                            context,
                            "File not found at path: $filePath",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            },
            colors = ButtonDefaults.buttonColors(primary),
            modifier = modifier.align(Alignment.CenterHorizontally)
        ) {
            if (!isPredicting && !isBack) {
                Text(text = "Analyze", modifier.padding(end = 4.dp))
                Icon(imageVector = Icons.Default.Analytics, contentDescription = null)
            } else {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier.padding(end = 4.dp))
                Text(text = "Back")
            }
        }


    }


//    // Tracking progress audio
//    LaunchedEffect(key1 = isPlaying.value) {
//        while (isPlaying.value) {
//            // Ambil progress audio saat ini
//            val currentPosition = mediaPlayer.currentPosition / 1000
//
//            // Log progress audio
//            Log.e("AudioPlayer", "Progress: $currentPosition")
//            Log.e("AudioPlayer", "Progressbar: $progress")
//
//            // Tunggu 1 detik
//            delay(1000)
//        }
//    }
//
//    DisposableEffect(Unit) {
//        // Ketika komponen dihancurkan, hentikan pemutaran audio
//        onDispose {
//            mediaPlayer.stop()
//            mediaPlayer.release()
//        }
//    }
}
