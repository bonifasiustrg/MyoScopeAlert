package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConnectBluetoothScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        Text(text = "Scanned Device List", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn() {
            items(15) {
                ListDeviceItem(it)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ListDeviceItem(it: Int) {
    var clicked by remember {
        mutableStateOf(false)
    }
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f)) {
            Text(text = "Device Name $it", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "UUID : ${it*10}:${it*12}:${it*14}:${it*15}")
        }
        Button(onClick = { clicked = !clicked }, colors = ButtonDefaults.buttonColors(
            containerColor = if (clicked) Color.Green else Color.DarkGray
        )) {
            if (clicked) {
                Text(text = "Paired")

            } else {

                Text(text = "Not Connected")
            }
        }
    }
}