@file:OptIn(ExperimentalMaterial3Api::class)

package com.apicta.myoscopealert.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.data.BottomNavigationItem
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.graphs.HomeNavGraph
import com.apicta.myoscopealert.models.DiagnosesViewModel
import com.apicta.myoscopealert.models.UserViewModel
import com.apicta.myoscopealert.ui.common.MainTopBar
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(dataStoreManager: DataStoreManager, navController: NavHostController = rememberNavController()) {
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

//    val bottomBarItems = listOf(
//        BottomNavigationItem(
//            title = "Home",
//            selectedIcon = Icons.Filled.Home,
//            unselectedIcon = Icons.Outlined.Home
//        ),
//        BottomNavigationItem(
//            title = "Record",
//            selectedIcon = Icons.Filled.Add,
//            unselectedIcon = Icons.Outlined.Add
//        ),
//        BottomNavigationItem(
//            title = "History",
//            selectedIcon = Icons.Filled.List,
//            unselectedIcon = Icons.Outlined.List
//        ),
//        BottomNavigationItem(
//            title = "Profile",
//            selectedIcon = Icons.Filled.Person,
//            unselectedIcon = Icons.Outlined.Person
//        ),
//
//        )
//    var selectedItemIndex by rememberSaveable {
//        mutableStateOf(0)
//    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) {
        Surface(modifier = Modifier.padding(it)) {
            HomeNavGraph(navController = navController, dataStoreManager = dataStoreManager)
        }
    }
    
//    Scaffold(
//        bottomBar = {
//            NavigationBar(
//                modifier = Modifier.clip(
//                    shape = RoundedCornerShape(
//                        topStart = 16.dp,
//                        topEnd = 16.dp
//                    )
//                ),
//                containerColor = primary
//            ) {
//                bottomBarItems.forEachIndexed() { index, item ->
//                    NavigationBarItem(
//                        selected = index == selectedItemIndex,
//                        onClick = {
//                            selectedItemIndex = index
//                            navController.navigate("${item.title.lowercase()}_screen")
//                        },
//                        colors = NavigationBarItemDefaults.colors(
//                            selectedIconColor = Color.White,
//                            indicatorColor = primary,
//                            unselectedIconColor = secondary
//                        ),
//                        icon = {
//                            Icon(
//                                imageVector = if (selectedItemIndex == index) {
//                                    item.selectedIcon
//                                } else {
//                                    item.unselectedIcon
//                                }, contentDescription = null
//                            )
//                        })
//                }
//            }
//        },
////        topBar = { MainTopBar("Dashboard", navController = navController) }
//    ) {
//        Surface(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(it)
//        ) {
////            Column(Modifier.fillMaxSize()) {
//////                Text(text = "Dashboard Screen", fontWeight = FontWeight.Bold)
////                if (diagnosesResponse == null) {
////                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
////                        CircularProgressIndicator()
////                    }
////                } else {
////                    Text(text = "Your Data", fontWeight = FontWeight.Bold)
////                    Text(
////                        text = "${diagnosesResponse!!.message}",
////                        fontWeight = FontWeight.Bold,
////                        color = Color.Green
////                    )
////
////                    Spacer(modifier = Modifier.height(32.dp))
////
////                    Text(text = "Doctor Profile", fontWeight = FontWeight.Bold)
////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.fullname}")
////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.id}")
////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.age}")
////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.address}")
////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.emergencyPhone}")
////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.gender}")
////
////
////                    val diagnoses = diagnosesResponse!!.data?.get(0)?.diagnoses
////                    val isVerified = diagnosesResponse!!.data?.get(0)?.isVerified
////                    Text(text = "Diagnoses", fontWeight = FontWeight.Bold)
////                    Text(
////                        text = "$diagnoses",
////                        modifier = Modifier.background(color = if (diagnoses == "normal") Color.Green else Color.Red)
////                    )
////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.id}")
////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.notes}")
////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.file}")
////                    Text(text = if (isVerified == "0") "Not Verified" else "Verified")
////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.updatedAt}")
//////                    Text(text = "${diagnosesResponse!!.data?.get(0)?.patient?.condition}")
////
////
////                    Button(
////                        onClick = {
////                            if (storedToken != null) {
////                                viewModelUser.performProfile(storedToken!!)
//////                            Log.e("login", "${viewModel.isLoginSuccess.value}} dan ${viewModel.loginResponse.value}")
////
////
////                                // Tambahkan penundaan selama misalnya 1 detik sebelum navigasi
////                                viewModelUser.viewModelScope.launch {
////                                    navController.navigate("profile_screen")
////                                    Log.e("dashboard", "navigate to profile")
////                                }
////                            } else {
////                                Log.e("dashboard to login", "can not to profile")
////                            }
////                        }
////                    ) {
////                        Text("Profile")
////                    }
////                }
////            }
//            HomeScreen(navController = navController)
//        }
//    }
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
                )
            ),
            containerColor = primary
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
            indicatorColor = primary,
            unselectedIconColor = secondary
        )
    )
}

@Preview
@Composable
fun DashboardScreenPrev() {
    DashboardScreen(
        navController = rememberNavController(), dataStoreManager = DataStoreManager(
            LocalContext.current
        )
    )
}