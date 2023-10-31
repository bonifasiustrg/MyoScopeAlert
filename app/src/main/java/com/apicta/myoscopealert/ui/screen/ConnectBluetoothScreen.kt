package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ConnectBluetoothScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Scanned Device List", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(32.dp))
        var no =3
        val names = listOf("STET-02", "SmartBand843", "SAMSUNG TV 34", "redmi wr")
        LazyColumn() {
            items(names) {
                ListDeviceItem(it, no)
                Spacer(modifier = Modifier.height(8.dp))
                Divider(modifier = Modifier.fillMaxWidth(),thickness = 2.dp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(8.dp))
                no++
            }
        }
    }
}

@Composable
fun ListDeviceItem(it: String, no: Int) {
    var clicked by remember {
        mutableStateOf(false)
    }
    var colorState by remember { mutableStateOf(Color.Red) }
    var statusState by remember { mutableStateOf("Not Connected") }
    val scope = rememberCoroutineScope()
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f)) {
            Text(text = it, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "MAC : ${no * 6}:${no * 12}:${no * 14}:${no * 15}")
        }
        Button(
            onClick = {
                clicked = !clicked
                scope.launch {
                    // Set color to yellow after 2 seconds
//                    delay(2000)
                    colorState = Color.Yellow
                    statusState = "Connecting..."

                    // Set color to green after another 2 seconds
                    delay(2000)
                    colorState = Color.Green
                    statusState = "Paired"
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (clicked) {
                    colorState
                } else Color.Gray
            )
        ) {
            Text(
                text = if (clicked) {
                    statusState
                } else "Not Connected",
                color = Color.Black
            )

        }
    }
}