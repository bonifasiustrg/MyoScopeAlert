package com.apicta.myoscopealert.ui.screen

import android.content.Context
import android.content.ContextWrapper
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.databinding.SignalChartBinding
import com.apicta.myoscopealert.models.FileModel
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun FileListScreen(navController: NavHostController) {
    val fileList = ArrayList<FileModel>()

    val directory =
        File("/storage/emulated/0/Android/data/com.apicta.myoscopealert/files/Recordings")
    val wavFiles = directory.listFiles { _, name -> name.endsWith(".wav") }
    val context = LocalContext.current
// Konversi array File menjadi daftar nama file .wav
    val wavFileNames = wavFiles?.map { it.name } ?: emptyList()

    val contextWrapper = ContextWrapper(context)
    val externalStorage: File =
        contextWrapper.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)!!

    val audioDirPath = externalStorage.absolutePath

    File(audioDirPath).walk().forEach {
        if (it.absolutePath.endsWith(".wav")) fileList.add(FileModel(it.name, it.lastModified()))
    }
    Log.e("filelist", "$fileList")
    Log.e("filelist", "$wavFileNames")
    Log.e("filelist", audioDirPath)

    LazyColumn {
        items(/*wavFileNames*/fileList) { file ->
            Row(
                Modifier
                    .fillMaxWidth()
//            .shadow(elevation = 4.dp, spotColor = Color.Gray, shape = RoundedCornerShape(16.dp))
                    .border(2.dp, secondary, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)

//            .padding(8.dp)
            ) {
                Column(Modifier.weight(2f)) {
                    Text(text = file.name.toString(), fontWeight = FontWeight.SemiBold)
                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

                    val formattedDate = file.date?.let { Date(it) }?.let { dateFormat.format(it) }
                    if (formattedDate != null) {
                        Text(text = formattedDate)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))


                Button(
                    onClick = {
                        navController.navigate("detail/${file.name}")

                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Lihat Hasil", fontWeight = FontWeight.Bold)
                    Icon(
                        painter = painterResource(id = R.drawable.ic_next_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }


}

@Composable
fun FileDetail(filename: String?) {
    val context = LocalContext.current

    val filePath =
        "/storage/emulated/0/Android/data/com.apicta.myoscopealert/files/Recordings/$filename"
    val isPlaying = remember {
        mutableStateOf(false)
    }
    val mediaPlayer = remember {
        MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
        }
    }
    var progress by remember { mutableStateOf(0f) }
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    // Set tombol play dan stop
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//        SetUpChart(context)

        ProcessWavFileData(filePath, context)
        // Menampilkan progress bar
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 16.dp),
            color = Color.Blue.copy(alpha = 0.2f)

        )
        Button(
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
                            var currentPosition = mediaPlayer.currentPosition.toFloat()
                            val totalDuration = mediaPlayer.duration.toFloat()
                            Log.e("progress", "$currentPosition | $totalDuration")
                            val progressPercentage = currentPosition / totalDuration
                            progress = progressPercentage
                            if (progress == 1.0f) {
                                isPlaying.value = false
                            }
                            delay(500)

                        }
                    }
                }
                isPlaying.value = !isPlaying.value
            },
            modifier = Modifier.padding(16.dp)
        ) {
            if (isPlaying.value) Text("Stop") else {
                Text("Play")
//                progress =0f
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
            Log.e("AudioPlayer", "Progressbar: ${progress}")

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
fun ProcessWavFileData(wavFilePath: String, ctx: Context) {
    val SAMPLE_RATE = 8000
    val SHRT_MAX = 32767

    AndroidViewBinding(SignalChartBinding::inflate) {
        signalView.description?.isEnabled = false
        signalView.setTouchEnabled(true)
        signalView.setPinchZoom(true)
        signalView.setBackgroundColor(getColor(ctx, R.color.white))
        signalView.setDrawGridBackground(false)

        // Customize X-axis properties if needed
        val xAxis = signalView?.xAxis
        xAxis?.setDrawGridLines(false)

        // Customize Y-axis properties if needed
        val yAxis = signalView?.axisLeft
        yAxis?.setDrawGridLines(false)

        val wavFile = File(wavFilePath)
        val audioData = ArrayList<Entry>()

        // Read the WAV file using AudioRecord
        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val wavData = ByteArray(bufferSize)
        val dataPoints = ArrayList<Float>()

        val inputStream = FileInputStream(wavFile)
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
        Log.e("processwav", "try n catch bytes read")

        // Convert data points to Entry objects for the chart
        for (i in dataPoints.indices) {
            val entry = Entry(i.toFloat(), dataPoints[i])
            audioData.add(entry)
        }
        Log.e("processwav", "Convert data points to Entry objects for the chart")

        // Create a LineDataSet with the audio data
        val dataSet = LineDataSet(audioData, "Heart Beat Signal")
        dataSet.color = R.color.green
        dataSet.setDrawCircles(false)
        Log.e("processwav", "Create a LineDataSet with the audio data")

        // Create a LineData object and set the LineDataSet
        val lineData = LineData(dataSet)
        Log.e("processwav", "Create a LineData object and set the LineDataSet")

        // Set the LineData object to the chart
        signalView?.data = lineData
        Log.e("processwav", "Set the LineData object to the chart")

        // Refresh the chart
        signalView?.invalidate()
        Log.e("processwav", "Refresh signalview")
    }
}

@Composable
fun SetUpChart(ctx: Context) {
    AndroidViewBinding(
        SignalChartBinding::inflate,
        modifier = Modifier.border(width = 2.dp, color = secondary, shape = RoundedCornerShape(16.dp))
    ) {
        // Configure chart properties
        signalView.description?.isEnabled = false
        signalView.setTouchEnabled(true)
        signalView.setPinchZoom(true)
        signalView.setBackgroundColor(getColor(ctx, R.color.white))
        signalView.setDrawGridBackground(false)

        // Customize X-axis properties if needed
        val xAxis = signalView?.xAxis
        xAxis?.setDrawGridLines(false)

        // Customize Y-axis properties if needed
        val yAxis = signalView?.axisLeft
        yAxis?.setDrawGridLines(false)
    }
}

private fun formatDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
    val outputFormat = SimpleDateFormat("d MMMM yyyy HH:mm", Locale.US)

    val date = inputFormat.parse(inputDate)
    return outputFormat.format(date)
}