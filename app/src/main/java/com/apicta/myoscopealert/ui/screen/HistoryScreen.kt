package com.apicta.myoscopealert.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.apicta.myoscopealert.ui.theme.primary

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HistoryScreen(navController: NavHostController) {
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
    var query by remember {
        mutableStateOf("")
    }
    val isShowing = remember {
        mutableStateOf(false)
    }

    Scaffold(
        floatingActionButton = {
            if (!isShowing.value) {

//            Button(onClick = { isShowing.value = true }) {
//                Text(text = "Open Dialog")
//            }
            FloatingActionButton(modifier = Modifier
                .padding(bottom = 16.dp),
                onClick = {
                    isShowing.value = true
                }) {
                Icon(
                    imageVector = Icons.Filled.Upload,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .padding(it)
        ) {
            Text(text = "Data Rekaman", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(
                        text = "Cari rekaman",
                        color = Color.Gray,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .fillMaxWidth(0.8f)
                    .height(54.dp)
                    .align(Alignment.CenterHorizontally)
                    .border(2.dp, primary, shape = RoundedCornerShape(50.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                textStyle = TextStyle(fontSize = 12.sp),

                trailingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))


//        LazyColumn() {
//            items(20) {
//                HistoryItem(navController)
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//        }

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
//        Spacer(modifier = Modifier.weight(1f))
//        FloatingActionButton(modifier = Modifier
//            .align(Alignment.CenterHorizontally)
//            .padding(bottom = 16.dp),
//            onClick = {
//                navController.navigate(BottomBarScreen.Record.route)
//        }) {
//            Icon(
//                imageVector = Icons.Filled.Mic,
//                contentDescription = null,
//                modifier = Modifier.size(36.dp)
//            )
//        }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPrev() {
//    HistoryScreen(navController = rememberNavController(), token = "tes")
}