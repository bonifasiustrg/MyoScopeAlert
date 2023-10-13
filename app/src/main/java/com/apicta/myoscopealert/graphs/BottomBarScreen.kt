package com.apicta.myoscopealert.graphs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ManageHistory
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.ManageHistory
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Send
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    object Home : BottomBarScreen(
        route = "HOME",
        title = "HOME",
        selectedIcon = Icons.Default.Home,
        unselectedIcon = Icons.Outlined.Home,
    )

    object Record : BottomBarScreen(
        route = "RECORD",
        title = "RECORD",
        selectedIcon = Icons.Filled.MonitorHeart,
        unselectedIcon = Icons.Outlined.MonitorHeart

    )
    object History : BottomBarScreen(
        route = "HISTORY",
        title = "HISTORY",
        selectedIcon = Icons.Filled.ManageHistory,
        unselectedIcon = Icons.Outlined.ManageHistory
    )
    object Profile : BottomBarScreen(
        route = "PROFILE",
        title = "PROFILE",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person

    )

}