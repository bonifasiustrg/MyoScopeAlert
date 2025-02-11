package com.apicta.myoscopealert.ui.screen.common

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun ChipCustom(label:String = "unknown status", color:Color = Color.Black) {

    AssistChip(
        onClick = { Log.d("Assist chip", "hello world") },
        label = { Text(label) },
        leadingIcon = {
            Icon(
                Icons.Filled.Info,
                contentDescription = "Localized description",
                Modifier.size(AssistChipDefaults.IconSize),
                tint = color
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color.White,
            labelColor = color
        ),
    )
}