package com.apicta.myoscopealert.ui.screen


import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Environment
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.ai.HeartbeatViewModel
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.models.diagnose.SingleDiagnoseResponse
import com.apicta.myoscopealert.ui.screen.common.CardContent
import com.apicta.myoscopealert.ui.screen.common.ProcessWavFileData
import com.apicta.myoscopealert.ui.screen.common.SetUpChart
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.terniary
import com.apicta.myoscopealert.ui.viewmodel.DiagnosesViewModel import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FileDetail(
    filename: String?,
    itemId: Int?,
//    fileDate: String?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DiagnosesViewModel = hiltViewModel(),
    heartbeatViewModel: HeartbeatViewModel = viewModel(),
//    viewModelProfile: UserViewModel = hiltViewModel()
) {

//    val storedToken by viewModel.userToken.collectAsState()
    val accountInfo by viewModel.accountInfo.collectAsState()
    Log.e("usertoken", "$accountInfo.token/*storedToken*/")
    Log.e("item id detail screen", "$itemId")

    if (itemId != -1) {

        viewModel.singleDiagnose(token = accountInfo!!.token.toString(), userId = accountInfo!!.userId!!, itemId = itemId)
    }
    val singleDiagnoseResponse by viewModel.singleDiagnoseResponse.collectAsState()
    Log.e("history response", singleDiagnoseResponse.toString())
    val isVerified = if (singleDiagnoseResponse?.detection?.verified == "yes") true else false

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isBack by remember { mutableStateOf(false) }


    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    val filePath = "${musicDir.absolutePath}/$filename"
    val file = File(filePath)
    Log.e("newBT save", "$filePath")
    var isPredicting by remember { mutableStateOf(false) }
    val isPlaying = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val loadingPredict = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(primary)
                .padding(16.dp)
        ) {
            FileDetailsInfo(filePath = filePath)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Heartbeat Graph",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = poppins
        )
        Spacer(modifier = Modifier.height(8.dp))
        var isZooming by remember { mutableStateOf(false) }

        if (file.exists()) {
            // Proses file jika file sudah ada
            ProcessWavFileData(filePath, LocalContext.current, isZooming)
        } else {
            // Tampilkan pesan atau indikator loading
            SetUpChart(ctx = context)
            Log.e("DetailScreen", "File not found at path: $filePath")
        }
//        ProcessWavFileData(filePath, context, isZooming)
//        IconButton(
//            onClick = { isZooming = !isZooming },
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        ) {
//            Icon(
//                imageVector = if (isZooming) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
//                contentDescription = null,
//                modifier = Modifier.size(24.dp)
//            )
//        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = terniary
            ),
            modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {

//            CardContent(if (filename != "Record17Oct.wav") isVerified else false)
            if (itemId != -1) {


                CardContent(isVerified, note = singleDiagnoseResponse?.detection?.notes.toString())
            } else {
//                Text(text = if (itemId != -1) isVerified.toString() else "wav is not uploaded yet", color = Color.White)
//                Text(text = if (itemId != -1) singleDiagnoseResponse?.detection?.notes.toString() else "Tidak ada catatan dari dokter", color = Color.White)
                Card(modifier = Modifier.align(Alignment.CenterHorizontally), colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )) {
                    Text(text = "File wav belum dikirim, klik Analyze terlebih dahulu", color = Color.Red, textAlign = TextAlign.Center, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
                    Text(text = "Tidak ada catatan dokter", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                }

            }
        }

        Spacer(modifier = modifier.height(6.dp))
        Text(
            text = "Predict Your Heart Health",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = poppins

        )
        val predictResponse by viewModel.predictResponse.collectAsState()

        if (loadingPredict.value) {
            CircularProgressIndicator(modifier = modifier.align(Alignment.CenterHorizontally))
        }
        // Convert LiveData to State
        val prediction by heartbeatViewModel.prediction.collectAsState()

        if (prediction != null) {
            loadingPredict.value = false

            Box(
                modifier = modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        if (prediction == "Normal") Color(0xFF72D99D) else Color(
                            0xFFFF6F6F
                        )
                    )
                    .padding(vertical = 14.dp, horizontal = 64.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.8f)

            ) {
                Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (prediction == "Normal") "Normal Heart" else "Myocardial Infarction",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )

                    Icon(
//                        imageVector = if (predictResponse!!.data.result == 0) Icons.Outlined.CheckCircle else Icons.Outlined.Close,
                        imageVector = if (prediction == "Normal") Icons.Outlined.CheckCircle else Icons.Outlined.Close,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = modifier.height(16.dp))
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            Text(
                text = "No prediction yet",
                style = MaterialTheme.typography.bodyLarge
            )
            }
        }
        Button(
            onClick = {
                loadingPredict.value = true
//                isLoading.value = true

//                isPredicting = true
                /*FUNGSI PREDICT*/
                val file = File(filePath)

                // Periksa apakah file ada
                if (file.exists()) {
                    scope.launch {
                        try {
                            // Lakukan prediksi secara offline
                            heartbeatViewModel.predictHeartbeatFromPath(filePath)
                            Log.e("Prediction", "Offline prediction done")

                            // Panggil performPredict untuk mengirim file WAV ke API
                            viewModel.performPredict(filePath)
                            Log.e("Prediction", "performPredict triggered to send WAV to API")

                            // Jika perlu, tambahkan delay untuk menunggu respons API
                            delay(3500)

                            // Setelah proses selesai, lakukan navigasi atau perbarui UI sesuai kebutuhan
                            isBack = true
                            Toast.makeText(
                                context,
                                "Analyzing your heartbeat...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Log.e("PredictionError", "Error performing prediction", e)
                        } finally {
                            loadingPredict.value = false
                        }
                    }
                } else {
                    // Tangani error jika file tidak ditemukan
                    Log.e("Prediction", "File not found at path: $filePath")
                    Toast.makeText(
                        context,
                        "File not found at path: $filePath",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadingPredict.value = false

                }
            },
            colors = ButtonDefaults.buttonColors(Color.Magenta),
            modifier = modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Predict (Offline)")
        }

        if (isLoading.value) {
            CircularProgressIndicator(modifier = modifier.align(Alignment.CenterHorizontally))
        }
        if (predictResponse != null) {
            isLoading.value = false
//            viewModel.sendWav(filePath, condition = predictResponse!!.result.toString().lowercase(), token = accountInfo!!.token.toString())
//            Log.e("pantau condition", predictResponse!!.result.toString().lowercase())
            if (itemId == -1) {
                LaunchedEffect(key1 = predictResponse) {
                    viewModel.sendWav(
                        filePath,
                        condition = predictResponse!!.result.toString().lowercase(),
                        token = accountInfo!!.token.toString()
                    )
                    Log.e("pantau condition", predictResponse!!.result.toString().lowercase())
                }
            }

            Box(
                modifier = modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
//                        if (predictResponse!!.data.result == 0) Color(0xFF72D99D) else Color(
//                            0xFFFF6F6F
//                        )
                        if (predictResponse!!.result == "Normal") Color(0xFF72D99D) else Color(
                            0xFFFF6F6F
                        )
                    )
                    .padding(vertical = 14.dp, horizontal = 64.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.8f)

            ) {
                Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (predictResponse!!.result == "Normal") "Normal Heart" else "Myocardial Infarction",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )

                    Icon(
//                        imageVector = if (predictResponse!!.data.result == 0) Icons.Outlined.CheckCircle else Icons.Outlined.Close,
                        imageVector = if (predictResponse!!.result == "Normal") Icons.Outlined.CheckCircle else Icons.Outlined.Close,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = modifier.height(16.dp))
        }
//        Button(
////            enabled = !isPredicting,
//            onClick = {
////                isPredicting = true
//                if (isBack) {
//                    navController.popBackStack()
//                    navController.navigate(BottomBarScreen.History.route) {
//                        // Pop up to the start destination of the graph
//                        popUpTo(navController.graph.startDestinationId) {
//                            // Pop all inclusive
//                            inclusive = true
//                        }
//                        // Avoid multiple copies of the same destination when re-selecting it
//                        launchSingleTop = true
//                        // Restore state when re-selecting a previously selected item
//                        restoreState = true
//                    }
//                } else if (!isPredicting) {
//                    isLoading.value = true
//
//                    isPredicting = true
//                    /*FUNGSI PREDICT*/
//                    val file = File(filePath)
//
//                    // Periksa apakah file ada
//                    if (file.exists()) {
//                        scope.launch {
//                            try {
//
//
//                                viewModel.performPredict(filePath)
//                                delay(3500)
//
//                                if (itemId == -1) {
//                                    navController.navigate(BottomBarScreen.History.route) {
//                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
//                                        launchSingleTop = false
//                                        restoreState = false
//                                    }
//                                }
//
//                            } catch (e: Exception) {
//                                Log.e("PredictionError", "Error performing prediction", e)
//                            }
//                        }
//
//                        Log.e("DiagnosesViewModel", "File sended at path: $filePath")
//                        Toast.makeText(context, "Analyzing your heartbeat...", Toast.LENGTH_SHORT)
//                            .show()
//                        isBack = true
//                        isPredicting = false
//                    } else {
//                        // Handle kesalahan jika file tidak ditemukan
//                        Log.e("DiagnosesViewModel", "File not found at path: $filePath")
//                        Toast.makeText(
//                            context,
//                            "File not found at path: $filePath",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                }
//
//            },
//            colors = ButtonDefaults.buttonColors(primary),
//            modifier = modifier.align(Alignment.CenterHorizontally)
//        ) {
//            if (!isPredicting && !isBack) {
//                Text(text = if (itemId != -1) "Re-predict" else "Predict", modifier.padding(end = 4.dp))
//                Icon(imageVector = Icons.Default.Analytics, contentDescription = null)
//            } else {
//                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier.padding(end = 4.dp))
//                Text(text = "See all history")
//            }
//        }
        Button(
//            enabled = !isPredicting,
            onClick = {
                navController.popBackStack()
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
            },
            colors = ButtonDefaults.buttonColors(primary),
            modifier = modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier.padding(end = 4.dp))
            Text(text = "Back to history")
        }
    }
}


/*INFORMASI DETAIL WAV FILE*/
// Data class untuk metadata file
data class FileMetadata(
    val fileName: String,
    val fileSize: Long,
    val lastModified: Date,
    val duration: String
)

// Helper untuk memformat ukuran file
fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (ln(size.toDouble()) / ln(1024.0)).toInt()
    return String.format("%.1f %s", size / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
}

// Composable untuk menampilkan satu baris informasi (label dan value)
@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Composable utama untuk menampilkan informasi detail file WAV
@Composable
fun FileDetailsInfo(filePath: String) {
    val file = File(filePath)
    var fileInfo by remember { mutableStateOf<FileMetadata?>(null) }

    LaunchedEffect(filePath) {
        if (file.exists()) {
            val fileSizeInBytes = file.length()
            val lastModified = Date(file.lastModified())
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(filePath)
                val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val durationMs = durationStr?.toLongOrNull() ?: 0L
                val durationFormatted = DateUtils.formatElapsedTime(durationMs / 1000)

                fileInfo = FileMetadata(
                    fileName = file.name,
                    fileSize = fileSizeInBytes,
                    lastModified = lastModified,
                    duration = durationFormatted
                )
            } catch (e: Exception) {
                e.printStackTrace()
                fileInfo = FileMetadata(
                    fileName = file.name,
                    fileSize = fileSizeInBytes,
                    lastModified = lastModified,
                    duration = "N/A"
                )
            } finally {
                retriever.release()
            }
        }
    }

    if (file.exists() && fileInfo != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = "File Name", value = fileInfo!!.fileName)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(label = "File Size", value = formatFileSize(fileInfo!!.fileSize))
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(label = "Duration", value = fileInfo!!.duration)
                Spacer(modifier = Modifier.height(8.dp))
                val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                InfoRow(label = "Last Modified", value = dateFormat.format(fileInfo!!.lastModified))
            }
        }
    } else {
        // Tampilan jika file tidak ditemukan atau info belum tersedia
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "File tidak ditemukan atau sedang mengambil informasi...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}