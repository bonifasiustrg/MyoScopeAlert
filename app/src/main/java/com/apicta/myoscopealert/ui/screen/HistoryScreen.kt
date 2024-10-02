package com.apicta.myoscopealert.ui.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.audiopicker.FilePickerDialog
import com.apicta.myoscopealert.audiopicker.PickerConfig
import com.apicta.myoscopealert.audiopicker.PickerType
import com.apicta.myoscopealert.audiopicker.PickerUtils.printToLog
import com.apicta.myoscopealert.downloader.AndroidDownloader
import com.apicta.myoscopealert.models.Audio
import com.apicta.myoscopealert.ui.theme.cardsecondary
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary
import com.apicta.myoscopealert.ui.theme.terniary
import com.apicta.myoscopealert.ui.viewmodel.AudioViewModel
import com.apicta.myoscopealert.ui.viewmodel.DiagnosesViewModel
import com.apicta.myoscopealert.ui.viewmodel.UIEvents
import com.apicta.myoscopealert.ui.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    onServiceStart: () -> Unit,
    modifier: Modifier = Modifier,
    diagnosesViewModel: DiagnosesViewModel = hiltViewModel(),
    viewModel: AudioViewModel = hiltViewModel()
) {


//    viewModel.performProfile(/*storedToken!!*/accountInfo?.token.toString())
    diagnosesViewModel.diagnoseHistory()
    val historyResponse by diagnosesViewModel.diagnoseHistoryResponse.collectAsState()
    Log.e("history response", historyResponse.toString())



//    val viewModel: AudioViewModel = hiltViewModel()
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val items = remember {
        mutableStateListOf(
            "RecordWav",
            "DefaultName",
            "tes"
        )
    }
    val config = lazy {
        PickerConfig(
            currentType = PickerType.Audio,
            storageTitle = "storageTitle",
            storageDescription = "storageDescription",
            galleryTitle = "galleryTitle",
            galleryDescription = "galleryDescription",
            supportRtl = true,
            maxSelection = 12,
            searchTextHint = "searchTextHint",
            searchTextHintStyle = TextStyle(textAlign = TextAlign.Right),
            enableCamera = false
        )
    }
    var query by remember {
        mutableStateOf("")
    }
    val isShowing = remember {
        mutableStateOf(false)
    }
    var isLoading by remember {
        mutableStateOf(true)
    }

    fun onQueryChanged(query: String): List<Audio> {
        return runBlocking { viewModel.filterAudioList(query) }
    }
    runBlocking {
        viewModel.loadAudioData()
    }
    val audioListState by viewModel.audioList.collectAsState()

    var audioListSearch by remember {
        mutableStateOf<List<Audio>>(emptyList())
    }

    LaunchedEffect(audioListState) {
        delay(1200)
        isLoading = false
    }
    Scaffold(modifier = modifier,
        floatingActionButton = {
            if (!isShowing.value) {
                FloatingActionButton(modifier = modifier
                    .padding(bottom = 16.dp),
                    containerColor = secondary,
                    shape = CircleShape,
                    onClick = {
                        isShowing.value = true
                    }) {
                    Icon(
                        imageVector = Icons.Default.LibraryMusic,
                        contentDescription = null,
                        tint = primary,
                        modifier = modifier.size(36.dp)
                    )
                }
            }
        },
        topBar = {
            Column {
                Spacer(modifier = modifier.height(8.dp))
                Text(
                    text = "Recording History",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = modifier.align(Alignment.CenterHorizontally)
                )
                DockedSearchBar(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    query = text,
                    onQueryChange = {
                        text = it
                        audioListSearch = onQueryChanged(it)
                    },
                    onSearch = {
                        items.add(text)
                        active = false
                    },
                    active = active,
                    onActiveChange = {
                        // Atur status aktif dan bersihkan filter saat pencarian dibatalkan
                        active = it
//                        if (!it) {
//                            audioListSearch = viewModel.filterAudioList("")
//                        }
                    },
                    placeholder = {
                        Text(text = "Search")
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                    },
                    trailingIcon = {
                        if (active) {
                            Icon(
                                modifier = modifier.clickable {
                                    if (text.isNotEmpty()) {
                                        text = ""
                                    } else {
                                        active = false
//                                         Bersihkan filter saat ikon ditutup
                                        audioListSearch = viewModel.filterAudioList("")
                                    }
                                },
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Icon"
                            )
                        }
                    },
                ) {
                    Surface {
                        Column(
                            modifier = modifier
                                .verticalScroll(rememberScrollState())
                        ) {

                            items.forEach {
                                Row(modifier = modifier
                                    .padding(all = 14.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        query = it
                                        audioListSearch = onQueryChanged(it)
                                    }
                                ) {
                                    Icon(
                                        modifier = modifier.padding(end = 10.dp),
                                        imageVector = Icons.Default.History,
                                        contentDescription = "History Icon"
                                    )
                                    Text(text = it)
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {

        Column(
            modifier
                .fillMaxSize()
                .padding(top = 12.dp)
                .padding(it)
        ) {

            FileListScreen(
                navController, query,
                progress = viewModel.progress,
                onProgress = { viewModel.onUiEvents(UIEvents.SeekTo(it)) },
                isAudioPlaying = viewModel.isPlaying,
                audiList = if (audioListSearch.isNotEmpty()) {
                    audioListSearch
                } else audioListState,
                currentPlayingAudio = viewModel.currentSelectedAudio,
                onStart = {
                    viewModel.onUiEvents(UIEvents.PlayPause)
                },
                onItemClick = {
                    viewModel.onUiEvents(UIEvents.SelectedAudioChange(it))
                    onServiceStart()
                },
//                onNext = {
//                    viewModel.onUiEvents(UIEvents.SeekToNext)
//                }
                modifier = modifier,
                isLoading = isLoading,
                historyData = historyResponse?.data
            )
            if (isShowing.value) FilePickerDialog(
                config = config.value,
                onDismissDialog = {
                    isShowing.value = false
                },
                selectedFiles = {
                    it.printToLog("selectedFiles")
                }
            )
        }
    }
}
