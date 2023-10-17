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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.outlined.ArrowRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
//    Log.e("filelist", "$fileList")
//    Log.e("filelist", "$wavFileNames")
//    Log.e("filelist", audioDirPath)

    LazyColumn {
        items(/*wavFileNames*/fileList) { file ->
            Row(
                Modifier
                    .fillMaxWidth()
//            .shadow(elevation = 4.dp, spotColor = Color.Gray, shape = RoundedCornerShape(16.dp))
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(color = secondary)
                    .padding(start = 16.dp , end = 16.dp)
//            .padding(8.dp)
            ) {
                Column(Modifier.weight(2f)) {
                    Text(text = file.name.toString(), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

                    val formattedDate = file.date?.let { Date(it) }?.let { dateFormat.format(it) }
                    if (formattedDate != null) {
                        Text(text = formattedDate, modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))


//                Button(
//                    modifier = Modifier
//                        .fillMaxHeight()
////                        .size(48.dp)
//                        .clip(RoundedCornerShape(16.dp))
//                        ,
//                    onClick = {
//                        navController.navigate("detail/${file.name}")
//
//                    }, colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.White,
//                        contentColor = primary
//                    ),
//                    shape = RectangleShape
//                ) {
//
////                    Text(text = "Lihat Hasil", fontWeight = FontWeight.Bold)
//                    Icon(
//                        imageVector =Icons.Outlined.PlayArrow,
//                        contentDescription = null,
//                        modifier = Modifier.size(52.dp),
//                        tint = primary
//                    )
//                }
                Button(
                    onClick = {
                        navController.navigate("detail/${file.name}")
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(text = "Detail", fontWeight = FontWeight.Bold)
                    Icon(
                        painter = painterResource(id = R.drawable.ic_next_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }


}

