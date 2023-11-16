package com.apicta.myoscopealert.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.models.FileModel
import com.apicta.myoscopealert.ui.theme.cardbg
import com.apicta.myoscopealert.ui.theme.cardsecondary
import com.apicta.myoscopealert.ui.theme.greenIcon
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.redIcon
import com.apicta.myoscopealert.ui.theme.secondary
import com.apicta.myoscopealert.ui.theme.terniary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("Range")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun FileListScreen(navController: NavHostController, query: String) {
    val fileList = ArrayList<FileModel>()

    val context = LocalContext.current

    fileListDir(context, fileList)



//    val audioDirPath = "/storage/emulated/0/Music/" // Specify the desired path
//
//    File(audioDirPath).walk().forEach {
//        if (it.absolutePath.endsWith(".wav")) fileList.add(FileModel(it.name, it.lastModified()))
//    }


    Log.e("filelist", "$fileList")
//    Log.e("filelist", "$wavFileNames")
//    Log.e("filelist", audioDirPath)
//    if (query.isEmpty())
//        fileList
//    else
//        fileList.filter { it.audioDirPath.lowercase().contains(query.lowercase()) }
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        items(/*wavFileNames*/fileList) { file ->
            Row(
                Modifier
                    .fillMaxWidth()
//            .shadow(elevation = 4.dp, spotColor = Color.Gray, shape = RoundedCornerShape(16.dp))
                    .clip(shape = RoundedCornerShape(16.dp))
//                    .background(color = terniary)

                    .background(
                        color = cardsecondary,
                        shape = RoundedCornerShape(16.dp)
                    )
//                    .background(
//                        color = Color(0xC1FFFFFF)
//                    )
                    .padding(start = 16.dp, end = 16.dp)
//            .padding(8.dp)
            ) {
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

                val formattedDate = file.date?.let { Date(it) }?.let { dateFormat.format(it) }

                val heartStatus by remember {
                    mutableStateOf(true)
                }
                Column(Modifier.weight(2f)) {
                    Text(text = file.name.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        if (formattedDate != null) {
                            Text(text = formattedDate, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.checkicon /*else R.drawable.ic_close*/),
                            contentDescription = null,
                            tint = greenIcon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (heartStatus) greenIcon else redIcon,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            if (heartStatus) {
                                Text(
                                    text = "Normal",
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.End,
                                    color = Color.White,
                                    fontFamily = poppins
                                )
                            }  else {
                                Text(
                                    text = "MI",
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = poppins

                                )
                            }
                        }


                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

//                Button(
//                    onClick = {
//                        navController.navigate("detail/${file.name}/${formattedDate}")
//                    }, colors = ButtonDefaults.buttonColors(
//                        containerColor = primary,
//                        contentColor = Color.White
//                    ),
//                    modifier = Modifier.align(Alignment.CenterVertically)
//                ) {
//                    Text(text = "Detail", fontWeight = FontWeight.Bold)
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_next_arrow),
//                        contentDescription = null,
//                        modifier = Modifier.size(28.dp),
//                        tint = Color.White
//                    )
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_next_arrow),
//                        contentDescription = null,
//                        modifier = Modifier.size(28.dp),
//                        tint = Color.White
//                    )
//                }

//                IconButton(
//                    onClick = {
//                        navController.navigate("detail/${file.name}/${formattedDate}")
//                    }, colors = IconButtonDefaults.iconButtonColors(
//                        containerColor = primary,
//                        contentColor = terniary
//                    ),
//                    modifier = Modifier.align(Alignment.CenterVertically)
//                ) {
////                    Text(text = "Detail", fontWeight = FontWeight.Bold)
//                    Icon(
//                        imageVector = Icons.Filled.ChevronRight,
//                        contentDescription = null,
//                        modifier = Modifier.size(28.dp)
//                    )
//                }

                IconButton(
                    onClick = {
                        navController.navigate("detail/${file.name}/${formattedDate}")
                    }, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = primary,
                        contentColor = terniary
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).align(Alignment.CenterVertically)
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }


}

fun fileListDir(context:Context, fileList: ArrayList<FileModel>) {
    val resolver = context.contentResolver
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATE_MODIFIED)
    val selection = MediaStore.Audio.Media.DATA + " like ? "

    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    val selectionArgs = arrayOf("%${musicDir.absolutePath}%")
//    val selectionArgs = arrayOf("%/storage/emulated/0/Music/%")

    val sortOrder = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"

    val cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder)

    cursor?.use {
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

        while (cursor.moveToNext()) {
            val name = cursor.getString(nameColumn)
            val date = cursor.getLong(dateColumn)

            if (name.endsWith(".wav")) fileList.add(FileModel(name, date))
        }
    }

    Log.e("newBT save", "$uri")

    Log.e("newBT filelist", "$musicDir")
    Log.e("newBT filelist2", "$selectionArgs")
}