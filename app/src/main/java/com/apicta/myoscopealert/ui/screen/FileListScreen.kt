package com.apicta.myoscopealert.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.apicta.myoscopealert.ui.theme.cardsecondary
import com.apicta.myoscopealert.ui.theme.greenIcon
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.redIcon
import com.apicta.myoscopealert.ui.theme.terniary
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
    onNext: () -> Unit,
) {
    var openController by remember {
        mutableStateOf(false)
    }
    var formattedDate by remember {
        mutableStateOf("")
    }
//    val fileList = ArrayList<FileModel>()
//    val context = LocalContext.current
//    fileListDir(context, fileList)
//
//    Log.e("filelist", "$fileList")
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        itemsIndexed(/*wavFileNames*/audiList) { index, file ->
            Row(
                Modifier
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
//                Text(text = file.title, modifier = Modifier.clickable {
//                    onItemClick(index)
//                })
//                Text(text = file.data, modifier = Modifier.clickable {
//                    onItemClick(index)
//                })
//                Spacer(modifier = Modifier.height(16.dp))

//                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
//
//                val formattedDate = file.dateModified?.let { Date(it) }?.let { dateFormat.format(it) }


                val timestamp: Long = file.dateModified * 1000L // Assuming the timestamp is in seconds, so multiply by 1000 to convert to milliseconds

                val date = Date(timestamp)
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                formattedDate = formatter.format(date)
                val heartStatus by remember {
                    mutableStateOf(true)
                }
                Column(Modifier.weight(2f)) {
                    Text(
                        text = file.title.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
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
                Spacer(modifier = Modifier.height(12.dp))

                IconButton(
                    onClick = {
                        openController = true
                        onItemClick(index)
                    }, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = primary,
                        contentColor = terniary
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterVertically)
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    if (openController) {
        BottomBarPlayer(
            progress = progress,
            onProgress = onProgress,
            audio = currentPlayingAudio,
            onStart = onStart,
            onNext = onNext,
            isAudioPlaying = isAudioPlaying
        )
    }
}

@Composable
fun BottomBarPlayer(
    progress: Float,
    onProgress: (Float) -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
) {
    BottomAppBar(
        content = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArtistInfo(
                        audio = audio,
                        modifier = Modifier.weight(1f),
                    )
                    MediaPlayerController(
                        isAudioPlaying = isAudioPlaying,
                        onStart = onStart,
                        onNext = onNext
                    )
                    Slider(
                        value = progress,
                        onValueChange = { onProgress(it) },
                        valueRange = 0f..100f
                    )

                }
            }
        }
    )
}

@Composable
fun MediaPlayerController(
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .padding(4.dp)
    ) {
        PlayerIconItem(
            icon = if (isAudioPlaying) Icons.Default.Pause
            else Icons.Default.PlayArrow
        ) {
            onStart()
        }
        Spacer(modifier = Modifier.size(8.dp))
        Icon(
            imageVector = Icons.Default.SkipNext,
            modifier = Modifier.clickable {
                onNext()
            },
            contentDescription = null
        )
    }
}

@Composable
fun ArtistInfo(
    modifier: Modifier = Modifier,
    audio: Audio,
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerIconItem(
            icon = Icons.Default.MusicNote,
            borderStroke = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
        ) {}
        Spacer(modifier = Modifier.size(4.dp))
        Column {
            audio.title?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            audio.artist?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Clip,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun PlayerIconItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    borderStroke: BorderStroke? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    Surface(
        shape = CircleShape,
        border = borderStroke,
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            },
        contentColor = color,
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }
    }
}
