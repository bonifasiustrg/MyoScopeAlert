package com.apicta.myoscopealert.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController, token: String = "") {
    var query by remember {
        mutableStateOf("")
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
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
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,

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

        FileListScreen(navController)
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

@Composable
fun HistoryItem(navController: NavHostController) {
    Row(
        Modifier
            .fillMaxWidth()
//            .shadow(elevation = 4.dp, spotColor = Color.Gray, shape = RoundedCornerShape(16.dp))
            .border(2.dp, Color.Black, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)

//            .padding(8.dp)
    ) {
        Column(Modifier.weight(2f)) {
            Text(text = "Ceritanya ini nama file", fontWeight = FontWeight.SemiBold)
            Text(text = "09 Juni 2023 17:08")
        }
        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = {
                navController.navigate("detail_history")
            }, colors = ButtonDefaults.buttonColors(
                containerColor = primary,
                contentColor = Color.White
            )
        ) {
            Text(text = "Lihat Hasil", fontWeight = FontWeight.Bold)
            Icon(
                painter = painterResource(id = R.drawable.ic_next_arrow),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPrev() {
//    HistoryScreen(navController = rememberNavController(), token = "tes")
}