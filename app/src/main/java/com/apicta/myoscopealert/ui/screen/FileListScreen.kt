package com.apicta.myoscopealert.ui.screen

import android.os.Build
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.models.Audio
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
    isLoading: Boolean
) {
    
    var openController by remember {
        mutableStateOf(false)
    }
    var formattedDate by remember {
        mutableStateOf("")
    }
    if (openController) {
        BottomBarPlayer(
            progress = progress,
            onProgress = onProgress,
            audio = currentPlayingAudio,
            onStart = onStart,
//            onNext = onNext,
            isAudioPlaying = isAudioPlaying
        )
    }
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        itemsIndexed(audiList,
            key = { _, item -> item.id.toString() }
        ) { index, file ->
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
                                navController.navigate("detail/${file.displayName}/${formattedDate}")

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
                                    imageVector = ImageVector.vectorResource(id = R.drawable.checkicon /*else R.drawable.ic_close*/),
                                    contentDescription = null,
                                    tint = greenIcon,
                                    modifier = modifier.size(18.dp)
                                )
                                Spacer(modifier = modifier.width(4.dp))
                                Box(
                                    modifier = modifier
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
                                    } else {
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
                        Spacer(modifier = modifier.height(12.dp))

                        IconButton(
                            onClick = {
                                openController = true
                                onItemClick(index)
                            }, colors = IconButtonDefaults.iconButtonColors(
                                containerColor = primary,
                                contentColor = terniary
                            ),
                            modifier = modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = modifier
                                    .size(32.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        Spacer(modifier = modifier.height(8.dp))
                    }
                    Spacer(modifier = modifier.height(8.dp))
                }
            )
        }
    }
}


