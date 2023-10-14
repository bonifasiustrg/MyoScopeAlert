@file:OptIn(ExperimentalMaterial3Api::class)

package com.apicta.myoscopealert.ui.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.graphs.MainNavGraph
import com.apicta.myoscopealert.models.DiagnosesViewModel
import com.apicta.myoscopealert.models.UserViewModel
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dataStoreManager: DataStoreManager,
    navController: NavHostController = rememberNavController()
) {
    var storedToken by remember { mutableStateOf<String?>(null) }
    Log.d("DashboardScreen1", "Stored Token: $storedToken")

    // Ambil token jika belum diinisialisasi
    if (storedToken == null) {
        runBlocking {
            storedToken = dataStoreManager.getAuthToken.first()
            Log.d("DashboardScreen runblocking", "Stored Token: $storedToken")
        }
    }

    storedToken?.let { Log.e("stored token dashboard", it) }
    val viewModel = hiltViewModel<DiagnosesViewModel>()
    val viewModelUser = hiltViewModel<UserViewModel>()
    Log.e("dashboard", "viewmodel init")
    viewModel.performDiagnoses(storedToken!!)

    val diagnosesResponse by viewModel.diagnosesResponse.collectAsState()
    Log.e("diagnosesResponse", diagnosesResponse.toString())

    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) {
        Surface(modifier = Modifier.padding(it)) {
            MainNavGraph(navController = navController, dataStoreManager = dataStoreManager)
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Record,
        BottomBarScreen.History,
        BottomBarScreen.Profile,
    )
    val selectedItemIndex = rememberSaveable {
        mutableStateOf(0)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        NavigationBar(
            modifier = Modifier.clip(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp
                )),
            containerColor = primary,
            contentColor = Color.White
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController,
                    selectedItemIndex = selectedItemIndex
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController,
    selectedItemIndex: MutableState<Int>
) {
    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
//            Icon(
//                imageVector = if (selectedItemIndex == index) {
//                    screen.selectedIcon
//                } else {
//                    screen.unselectedIcon
//                }, contentDescription = null
//            )
            Icon(
                imageVector = screen.selectedIcon,
                contentDescription = "Navigation Icon"
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            indicatorColor = primary,
            unselectedIconColor = secondary,
            unselectedTextColor = secondary
        )
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun DashboardScreenPrev() {
    DashboardScreen(
        navController = rememberNavController(), dataStoreManager = DataStoreManager(
            LocalContext.current
        )
    )
}