@file:OptIn(ExperimentalMaterial3Api::class)

package com.apicta.myoscopealert.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.graphs.MainNavGraph
import com.apicta.myoscopealert.ui.theme.hover
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DashboardScreen(
    dataStoreManager: DataStoreManager,
    navController: NavHostController = rememberNavController(),
    onServiceStart: () -> Unit,
    modifier: Modifier = Modifier
) {
//    val token by dataStoreManager.getAuthToken.collectAsState(initial = "")
    val token by dataStoreManager.getAuthToken.collectAsState(initial = "")
    Log.e("Dashboard token classtate", "Stored Token: $token")
    val context = LocalContext.current

//    val launcher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            // Permission Accepted: Do something
//            Log.d("ExampleScreen","PERMISSION GRANTED")
//
//        } else {
//            // Permission Denied: Do something
//            Log.d("ExampleScreen","PERMISSION DENIED")
//        }
//    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap: Map<String, Boolean> ->
        permissionsMap.entries.forEach {
            if (it.value) {
                // Permission Accepted: Do something
                Log.d("ExampleScreen","${it.key} PERMISSION GRANTED")
            } else {
                // Permission Denied: Do something
                Log.d("ExampleScreen","${it.key} PERMISSION DENIED")
            }
        }
    }
    SideEffect {
        // Check permission
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
        val permissionNotGranted = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionNotGranted.isNotEmpty()) {
            // Asking for permission
            launcher.launch(permissionNotGranted.toTypedArray())
        } else {
            // Some works that require permission
            Log.d("ExampleScreen","Code requires permission")
        }
    }


//    SideEffect {
//
//        when (PackageManager.PERMISSION_GRANTED) {
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.BLUETOOTH_SCAN
//            ) -> {
//                // Some works that require permission
//                Log.d("ExampleScreen","Code requires permission")
//            }
//            else -> {
//                // Asking for permission
//                launcher.launch(Manifest.permission.BLUETOOTH_SCAN)
//            }
//        }
//    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController, modifier = modifier) }
        ) {
        Surface(modifier = modifier.padding(it)) {
            MainNavGraph(navController = navController, dataStoreManager = dataStoreManager
            ) { onServiceStart() }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, modifier:Modifier) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Record,
        BottomBarScreen.History,
        BottomBarScreen.Profile,
    )
    val selectedItemIndex = rememberSaveable {
        mutableIntStateOf(0)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        NavigationBar(
            modifier = modifier.clip(
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
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        icon = {
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
            indicatorColor = hover,
            unselectedIconColor = secondary,
            unselectedTextColor = secondary
        ),
        label = {
            Text(text = screen.title)
        },
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun DashboardScreenPrev() {
}