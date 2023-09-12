package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun HistoryScreen(navController: NavHostController, token: String) {
    Column {
        Text(text = "History Screen")
    }
}