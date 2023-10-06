package com.apicta.myoscopealert.graphs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
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
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add

    )
    object History : BottomBarScreen(
        route = "HISTORY",
        title = "HISTORY",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List
    )
    object Profile : BottomBarScreen(
        route = "PROFILE",
        title = "PROFILE",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person

    )

}