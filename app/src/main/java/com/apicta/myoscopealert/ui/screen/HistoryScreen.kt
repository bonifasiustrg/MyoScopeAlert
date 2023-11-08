package com.apicta.myoscopealert.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.audiopicker.FilePickerDialog
import com.apicta.myoscopealert.audiopicker.PickerConfig
import com.apicta.myoscopealert.audiopicker.PickerType
import com.apicta.myoscopealert.audiopicker.PickerUtils.printToLog
import com.apicta.myoscopealert.ui.theme.cardbg
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary
import com.apicta.myoscopealert.ui.theme.terniary

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HistoryScreen(navController: NavHostController) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val items = remember {
        mutableStateListOf(
            "Record17Oct.wav",
            "recordwave1"
        )
    }
    val config = PickerConfig(
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
    val query by remember {
        mutableStateOf("")
    }
    val isShowing = remember {
        mutableStateOf(false)
    }

    Scaffold(modifier = Modifier,
        floatingActionButton = {
            if (!isShowing.value) {

//            Button(onClick = { isShowing.value = true }) {
//                Text(text = "Open Dialog")
//            }
            FloatingActionButton(modifier = Modifier
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
                    modifier = Modifier.size(36.dp)
                )
            }
            }
        }
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .padding(it)
        ) {
            Text(text = "Data Rekaman", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.align(Alignment.CenterHorizontally))

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                query = text,
                onQueryChange = {
                    text = it
                },
                onSearch = {
                    items.add(text)
                    active = false
                    text = ""
                },
                active = active,
                onActiveChange = {
                    active = it
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
                            modifier = Modifier.clickable {
                                if (text.isNotEmpty()) {
                                    text = ""
                                } else {
                                    active = false
                                }
                            },
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Icon"
                        )
                    }
                }
            ) {
                items.forEach {
                    Row(modifier = Modifier.padding(all = 14.dp)) {
                        Icon(
                            modifier = Modifier.padding(end = 10.dp),
                            imageVector = Icons.Default.History,
                            contentDescription = "History Icon"
                        )
                        Text(text = it)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FileListScreen(navController, query)
            if (isShowing.value) FilePickerDialog(
                config = config,
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