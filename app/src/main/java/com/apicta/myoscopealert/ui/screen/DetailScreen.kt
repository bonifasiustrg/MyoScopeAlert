package com.apicta.myoscopealert.ui.screen


import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
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
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.databinding.SignalChartBinding
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.ui.viewmodel.DiagnosesViewModel
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary
import com.apicta.myoscopealert.ui.theme.terniary
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException


@Composable
fun FileDetail(
    filename: String?,
    fileDate: String?,
    /*dataStoreManager: DataStoreManager,*/
    navController: NavHostController,
) {
    val viewModel: DiagnosesViewModel = hiltViewModel()
    val storedToken by viewModel.userToken.collectAsState()
    Log.e("usertoken", "$storedToken")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
//    var storedToken by remember { mutableStateOf<String?>(null) }
//    // Ambil token jika belum diinisialisasi
//    if (storedToken == null) {
//        runBlocking {
//            storedToken = dataStoreManager.getAuthToken.first()
//            Log.d("DetailScreen runblocking", "Stored Token: $storedToken")
//        }
//    }
//    storedToken?.let { Log.e("stored token dashboard", it) }


    var isBack by remember { mutableStateOf(false) }

    val filePath = "/storage/emulated/0/Android/data/com.apicta.myoscopealert/files/Recordings/$filename"
    val isPlaying = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    val mediaPlayer = remember {
        MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
        }
    }
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
    ).value

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
    // Set tombol play dan stop
    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {


        Column(
            Modifier
                .fillMaxWidth()
                .background(primary)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$filename",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(text = "Dokter      : Saparudin ", color = Color.White)
            Text(text = "Tanggal    : $fileDate", color = Color.White)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Grafik Detak Jantung", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = poppins)
        Spacer(modifier = Modifier.height(8.dp))

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
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = if (isZooming) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 28.dp),
            color = Color.Blue.copy(alpha = 0.2f)
        )
        Button(
            shape = CircleShape,
            onClick = {
                // Mulai atau berhenti memutar audio
                if (isPlaying.value) {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                    }
                } else {
                    mediaPlayer.start()
                    // Memantau progress audio
                    CoroutineScope(Dispatchers.IO).launch {
                        while (isPlaying.value) {
                            val currentPosition = mediaPlayer.currentPosition.toFloat()
                            val totalDuration = mediaPlayer.duration.toFloat()
                            Log.e("progress", "$currentPosition | $totalDuration")
                            val progressPercentage = currentPosition / totalDuration
                            progress = progressPercentage
                            if (progress == 1.0f) {
                                isPlaying.value = false
                            }
                            if (isPlaying.value && mediaPlayer.currentPosition >= mediaPlayer.duration) {
                                // Audio telah diputar sampai selesai
                                isPlaying.value = false
                                // Tampilkan pesan ke pengguna jika diperlukan
                            }
                            delay(200)

                        }
                    }
                }
                isPlaying.value = !isPlaying.value
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Icon(
                imageVector = if (isPlaying.value) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        val isVerified by remember {
            mutableStateOf(true)
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = terniary
            ),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            CardContent(if (filename != "Record17Oct.wav") isVerified else false)
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Prediksi Status Kesehatan Jantung",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = poppins

        )
        var isPredicting by remember { mutableStateOf(false) }
        val predictResponse by viewModel.predictResponse.collectAsState()
//        LaunchedEffect(predictResponse) {
//            // Set isPredicting menjadi false saat nilai predictResponse berubah
//            isLoading.value == false
//        }

        if (predictResponse != null) {
            isLoading.value = false

            Box(
                modifier = Modifier
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (predictResponse!!.result == 0) "Jantung Normal" else "myocardial infarction",
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
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
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
                        Toast.makeText(context, "Your data is processing...", Toast.LENGTH_SHORT)
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
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            if (!isPredicting && !isBack) {
                Icon(imageVector = Icons.Default.Analytics, contentDescription = null)
                Text(text = "Prediksi")
            } else {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                Text(text = "Kembali")
            }
        }


    }


    // Tracking progress audio
    LaunchedEffect(key1 = isPlaying.value) {
        while (isPlaying.value) {
            // Ambil progress audio saat ini
            val currentPosition = mediaPlayer.currentPosition / 1000

            // Log progress audio
            Log.e("AudioPlayer", "Progress: $currentPosition")
            Log.e("AudioPlayer", "Progressbar: $progress")

            // Tunggu 1 detik
            delay(1000)
        }
    }

    DisposableEffect(Unit) {
        // Ketika komponen dihancurkan, hentikan pemutaran audio
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}

@Composable
fun ProcessWavFileData(wavFilePath: String, ctx: Context, isZooming: Boolean = false) {
    val SAMPLE_RATE = 8000
    val SHRT_MAX = 32767
    val audioData = ArrayList<Entry>()
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {

        AndroidViewBinding(SignalChartBinding::inflate) {
            signalView.description?.isEnabled = false
            signalView.setTouchEnabled(true)
            signalView.setPinchZoom(true)
            signalView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
            signalView.setDrawGridBackground(false)

            // Customize X-axis properties if needed
            val xAxis = signalView.xAxis
            xAxis?.setDrawGridLines(false)

            // Customize Y-axis properties if needed
            val yAxis = signalView.axisLeft
            yAxis?.setDrawGridLines(false)

            if (isZooming) {
                yAxis?.setAxisMaximum(0.03f)
                yAxis?.setAxisMinimum(-0.03f)
            }

            // **Memory Efficiency Improvement:** Read the WAV file in chunks instead of loading the entire file into memory at once.
            val bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val wavData = ByteArray(bufferSize)
            val dataPoints = ArrayList<Float>()

            val inputStream = FileInputStream(wavFilePath)
            val bufferedInputStream = BufferedInputStream(inputStream)
            val dataInputStream = DataInputStream(bufferedInputStream)


            try {
                var bytesRead = dataInputStream.read(wavData, 0, bufferSize)

                while (bytesRead != -1) {
                    // Process the WAV data and convert it to data points suitable for the chart
                    for (i in 0 until bytesRead / 2) { // Assuming 16-bit PCM
                        val sample =
                            wavData[i * 2].toInt() and 0xFF or (wavData[i * 2 + 1].toInt() shl 8)
                        val amplitude = sample.toFloat() / SHRT_MAX.toFloat() // Normalize amplitude
                        dataPoints.add(amplitude)
                    }

                    bytesRead = dataInputStream.read(wavData, 0, bufferSize)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                dataInputStream.close()
            }

            // **Memory Leak Prevention:** Close the WAV file after processing it.
            inputStream.close()


            // Convert data points to Entry objects for the chart
            for (i in dataPoints.indices) {
                val entry = Entry(i.toFloat(), dataPoints[i])
                audioData.add(entry)
            }

            // Create a LineDataSet with the audio data
            val dataSet = LineDataSet(audioData, "Heart Beat Wave")
            dataSet.color = R.color.green
            dataSet.setDrawCircles(false)

            // Create a LineData object and set the LineDataSet
            val lineData = LineData(dataSet)

            // Set the LineData object to the chart
            signalView.data = lineData

            // moveViewToX(...) also calls invalidate()

            // Refresh the chart
            signalView.invalidate()

            if (isZooming) {
                // now modify viewport
                signalView.setVisibleXRangeMaximum(10000F) // allow 20 values to be displayed at once on the x-axis, not more
                signalView.moveViewToX(100F) // set the left edge of the chart to x-index 10
            }

            Log.e("processwav", "Refresh signalview")
        }


    }
}


@Composable
fun SetUpChart(ctx: Context) {
    AndroidViewBinding(
        SignalChartBinding::inflate,
        modifier = Modifier.border(
            width = 2.dp,
            color = secondary,
            shape = RoundedCornerShape(16.dp)
        )
    ) {
        // Configure chart properties
        signalView.description?.isEnabled = false
        signalView.setTouchEnabled(true)
        signalView.setPinchZoom(true)
        signalView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
        signalView.setDrawGridBackground(false)

        // Customize X-axis properties if needed
        val xAxis = signalView.xAxis
        xAxis?.setDrawGridLines(false)

        // Customize Y-axis properties if needed
        val yAxis = signalView.axisLeft
        yAxis?.setDrawGridLines(false)
    }
}

@Composable
private fun CardContent(isVerified: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
//            Text(text = "Hello, ")
            Row {

                Text(
                    text = "Hasil verifikasi Dokter", style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppins

                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isVerified) Color.Green else Color.Red,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    if (isVerified) {
                        Text(
                            text = "Verified",
                            fontSize = 10.sp,
                            textAlign = TextAlign.End,
                            color = Color.Black,
                            fontFamily = poppins
                        )
                    }  else {
                        Text(
                            text = "Not verified",
                            fontSize = 10.sp,
                            textAlign = TextAlign.End,
                            color = Color.White,
                            fontFamily = poppins

                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (expanded) {
                Text(
                    text = "Setelah memeriksa grafik gelombang suara detak jantung pasien, saya mengkonfirmasi bahwa tidak terdapat indikasi penyakit Myocardial infarction. Kondisi jantung pasien terlihat sehat dan stabil berdasarkan analisis grafik yang telah kami verifikasi.",
                    fontFamily = poppins

                )
            }
        }

        if(isVerified) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }
        }
    }
}
