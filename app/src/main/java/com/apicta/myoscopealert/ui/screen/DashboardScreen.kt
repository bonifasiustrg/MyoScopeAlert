package com.apicta.myoscopealert.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.apicta.myoscopealert.data.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun DashboardScreen() {
    val ctx = LocalContext.current
    val dataStoreManager = DataStoreManager.getInstance(ctx)
    val storedToken = runBlocking { dataStoreManager?.getAuthToken?.first() }
    Column {
        Text("Welcome to Myoscope Alert!", fontSize = 48.sp)
        Log.e("token dashoboardd", storedToken.toString())
    }

}