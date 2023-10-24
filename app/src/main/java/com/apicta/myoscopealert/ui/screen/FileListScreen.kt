package com.apicta.myoscopealert.ui.screen

import android.content.ContextWrapper
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.models.FileModel
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun FileListScreen(navController: NavHostController, query: String) {
    val fileList = ArrayList<FileModel>()

    val context = LocalContext.current

    val contextWrapper = ContextWrapper(context)
    val externalStorage: File = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)!!

    val audioDirPath = externalStorage.absolutePath

    File(audioDirPath).walk().forEach {
        if (it.absolutePath.endsWith(".wav")) fileList.add(FileModel(it.name, it.lastModified()))
    }
//    Log.e("filelist", "$fileList")
//    Log.e("filelist", "$wavFileNames")
    Log.e("filelist", audioDirPath)
//    if (query.isEmpty())
//        fileList
//    else
//        fileList.filter { it.audioDirPath.lowercase().contains(query.lowercase()) }
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
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

                val formattedDate = file.date?.let { Date(it) }?.let { dateFormat.format(it) }
                Column(Modifier.weight(2f)) {
                    Text(text = file.name.toString(), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                    if (formattedDate != null) {
                        Text(text = formattedDate, modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        navController.navigate("detail/${file.name}/${formattedDate}")
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

