package com.apicta.myoscopealert.ui.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.downloader.AndroidDownloader
import com.apicta.myoscopealert.models.Audio
import com.apicta.myoscopealert.models.diagnose.DiagnoseHistoryResponse
import com.apicta.myoscopealert.ui.screen.common.ShimmerListItem
import com.apicta.myoscopealert.ui.theme.cardsecondary
import com.apicta.myoscopealert.ui.theme.greenIcon
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.redIcon
import com.apicta.myoscopealert.ui.theme.terniary
import com.apicta.myoscopealert.ui.viewmodel.AudioViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ColumnScope.FileListScreen(
    navController: NavHostController,
    query: String, progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: Audio,
    audiList: List<Audio>,
    onStart: () -> Unit,
    onItemClick: (Int) -> Unit,
//    onNext: () -> Unit,
    modifier:Modifier,
    isLoading: Boolean,
    historyData: List<DiagnoseHistoryResponse.Data?>?
) {
    val reversedHistoryData = historyData?.toMutableList()?.apply { reverse() } ?: emptyList()

    val ctx = LocalContext.current
    val downloader = AndroidDownloader(ctx)
    var openController by remember {
        mutableStateOf(false)
    }
    var formattedDate by remember {
        mutableStateOf("")
    }

    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (historyData != null) {
            items(historyData.asReversed()){
                val fileName = it?.heartwave?.substringAfterLast("/")
//                downloader.downloadFile("https://miocardial.humicprototyping.com/myocardial_baru/storage/app/public/heartwaves/$fileName")
                // Trigger download satu kali per fileName
                LaunchedEffect(key1 = fileName) {
                    downloader.downloadFile("https://miocardial.humicprototyping.com/myocardial_baru/storage/app/public/heartwaves/$fileName")
                }
                ShimmerListItem(
                    isLoading = isLoading,
                    contentAfterLoading = {
                        Row(
                            modifier
                                .fillMaxWidth()
                                .clip(shape = RoundedCornerShape(16.dp))

                                .background(
                                    color = cardsecondary,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
//                                    navController.navigate("detail/${file.displayName}/${formattedDate}")
//                                    navController.navigate("detail/${fileName}/${it?.created_at}")
//                                    if (it != null) {
//                                        navController.navigate("detail/${fileName}/${it.id}")
//                                    } else {
//                                        navController.navigate("detail/${fileName}/-1")
//                                    }
                                    navController.navigate("detail/${fileName}/${it?.id ?: -1}")

                                }
                                .padding(start = 16.dp, end = 16.dp)
                        ) {

//                            val timestamp: Long = file.dateModified * 1000L // Assuming the timestamp is in seconds, so multiply by 1000 to convert to milliseconds

//                            val date = Date(timestamp)
//                            formattedDate = formatter.format(date)
                            val heartStatus by remember {
                                mutableStateOf(true)
                            }
                            Column(modifier.weight(2f)) {
                                Text(
                                    text = fileName.toString(),
                                    fontWeight = FontWeight.Bold,
                                    modifier = modifier.padding(vertical = 8.dp)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = modifier.padding(bottom = 8.dp)
                                ) {
//                                    Text(text = formattedDate, fontSize = 12.sp)
                                    val dateTimeString =  it?.created_at.toString()

//                                    // Parsing ISO 8601 format
//                                    val dateTime = LocalDateTime.parse(dateTimeString.substring(0, 19))
//
//                                    // Format ke bentuk umum: dd-MM-yyyy HH:mm:ss
//                                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
//                                    val formattedDateTime = dateTime.format(formatter)

                                    // Define the formatter matching the input string
                                    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                                    // Parse the input string to LocalDateTime
                                    val dateTime = LocalDateTime.parse(dateTimeString, inputFormatter)

                                    // Define the desired output format
                                    val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

                                    // Format the LocalDateTime to the desired string
                                    val formattedDateTime = dateTime.format(outputFormatter)

                                    // Display the formatted date-time
                                    Text(text = formattedDateTime, fontSize = 12.sp)
                                    Spacer(modifier = modifier.width(4.dp))
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = if (it?.verified == "yes") R.drawable.checkicon else R.drawable.ic_close),
                                        contentDescription = null,
                                        tint = if (it?.verified == "yes") greenIcon else redIcon,
                                        modifier = modifier.size(18.dp)
                                    )
                                    Spacer(modifier = modifier.width(4.dp))
                                    Box(
                                        modifier = modifier
                                            .background(
                                                color = if (it?.condition?.lowercase() == "normal") greenIcon else redIcon,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .padding(vertical = 4.dp, horizontal = 8.dp),
                                    ) {
                                        if (it?.condition?.lowercase() == "normal") {
                                            Text(
                                                text = "Normal",
                                                fontSize = 10.sp,
                                                textAlign = TextAlign.End,
                                                color = Color.White,
                                                fontFamily = poppins
                                            )
                                        } else if (it?.condition?.lowercase() == "mi detected") {
                                            Text(
                                                text = "MI",
                                                fontSize = 10.sp,
                                                textAlign = TextAlign.End,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontFamily = poppins

                                            )
                                        } else {
                                            Text(
                                                text = "Not yet predicted",
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
                            Spacer(modifier = modifier.height(12.dp))

                            Spacer(modifier = modifier.height(8.dp))
                        }
                        Spacer(modifier = modifier.height(8.dp))
                    }
                )
            }
        }



        val filteredAudioList = audiList.filterNot { audio ->
            historyData?.any { history ->
                val historyFileName = history?.heartwave?.substringAfterLast("/")
                audio.displayName == historyFileName
            } ?: false
        }.toMutableList().asReversed()
        itemsIndexed(filteredAudioList, key = { _, item -> item.id.toString() }) { index, file ->

            ShimmerListItem(
                isLoading = isLoading,
                contentAfterLoading = {
                    Row(
                        modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(16.dp))

                            .background(
                                color = cardsecondary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
//                                    navController.navigate("detail/${file.displayName}/${formattedDate}")
//                                navController.navigate("detail/${fileName}/${it?.created_at}")
                                navController.navigate("detail/${file.displayName}/${-1}")
                            }
                            .padding(start = 16.dp, end = 16.dp)
                    ) {

                        val timestamp: Long = file.dateModified * 1000L // Assuming the timestamp is in seconds, so multiply by 1000 to convert to milliseconds

                        val date = Date(timestamp)
                        formattedDate = formatter.format(date)
                        val heartStatus by remember {
                            mutableStateOf(true)
                        }
                        Column(modifier.weight(2f)) {
                            Text(
                                text = file.title.toString(),
                                fontWeight = FontWeight.Bold,
                                modifier = modifier.padding(vertical = 8.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = modifier.padding(bottom = 8.dp)
                            ) {
                                Text(text = formattedDate, fontSize = 12.sp)

                                Spacer(modifier = modifier.width(4.dp))
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_close),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = modifier.size(18.dp)
                                )
                                Spacer(modifier = modifier.width(4.dp))
                                Box(
                                    modifier = modifier
                                        .background(
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                ) {
                                        Text(
                                            text = "Null",
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.End,
                                            color = Color.White,
                                            fontFamily = poppins
                                        )

                                }


                            }
                        }
                        Spacer(modifier = modifier.height(12.dp))

                        Spacer(modifier = modifier.height(8.dp))
                    }
                    Spacer(modifier = modifier.height(8.dp))
                }
            )
        }
    }
}


