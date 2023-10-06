package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.data.BottomNavigationItem
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.graphs.HomeNavGraph
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(dataStoreManager: DataStoreManager, navController: NavHostController = rememberNavController()) {
//
//    Scaffold(
//        bottomBar = { BottomBar(navController = navController) }
//    ) {
//        Surface(modifier = Modifier.padding(it)) {
//            HomeNavGraph(navController = navController, dataStoreManager = dataStoreManager)
//        }
//    }
//}



//@Composable
//fun BottomBar(navController: NavHostController) {
//    val screens = listOf(
//        BottomBarScreen.Home,
//        BottomBarScreen.Record,
//        BottomBarScreen.History,
//        BottomBarScreen.Profile,
//    )
//    var selectedItemIndex = rememberSaveable {
//        mutableStateOf(0)
//    }
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination = navBackStackEntry?.destination
//
//    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
//    if (bottomBarDestination) {
//        NavigationBar(
//            modifier = Modifier.clip(
//                shape = RoundedCornerShape(
//                    topStart = 16.dp,
//                    topEnd = 16.dp
//                )
//            ),
//            containerColor = primary
//        ) {
//            screens.forEach { screen ->
//                AddItem(
//                    screen = screen,
//                    currentDestination = currentDestination,
//                    navController = navController,
//                    selectedItemIndex = selectedItemIndex
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun RowScope.AddItem(
//    screen: BottomBarScreen,
//    currentDestination: NavDestination?,
//    navController: NavHostController,
//    selectedItemIndex: MutableState<Int>
//) {
//    NavigationBarItem(
//        label = {
//            Text(text = screen.title)
//        },
//        icon = {
////            Icon(
////                imageVector = if (selectedItemIndex == index) {
////                    screen.selectedIcon
////                } else {
////                    screen.unselectedIcon
////                }, contentDescription = null
////            )
//            Icon(
//                imageVector = screen.selectedIcon,
//                contentDescription = "Navigation Icon"
//            )
//        },
//        selected = currentDestination?.hierarchy?.any {
//            it.route == screen.route
//        } == true,
//        onClick = {
//            navController.navigate(screen.route) {
//                popUpTo(navController.graph.findStartDestination().id)
//                launchSingleTop = true
//            }
//        },
//        colors = NavigationBarItemDefaults.colors(
//            selectedIconColor = Color.White,
//            indicatorColor = primary,
//            unselectedIconColor = secondary
//        )
//    )
//}



@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Hallo,")
        Text(
            text = "Sutejo Goodman,",
            fontStyle = MaterialTheme.typography.displayLarge.fontStyle,
            fontFamily = poppins,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .background(color = secondary, shape = RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 4.dp, bottom = 0.dp)
                    .size(120.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.FillHeight
            )
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp)) {
                Text(text = "Bagaimana perasaan Anda?", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text(text = "Mari mulai rekam jantung anda, untuk mengetahui kesehatan jantung anda", fontSize = 12.sp)

                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Mulai Rekam")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hasil Rekaman Terakhir",
            fontStyle = MaterialTheme.typography.labelLarge.fontStyle,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(16.dp))
                .background(color = secondary)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xC1FFFFFF),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)

            ) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_time),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp),
                        tint = Color.Yellow
                    )
                    Text(text = "28 September 2023")
                }
                Row {
                    Text(text = "oleh ")
                    Text(text = "Dokter Saparudin", fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.chart),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Myocardial Infarction")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp),
                        tint = Color.Red
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Negatif")
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green
                    )
                }
            }


        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .background(color = Color.Transparent, shape = RoundedCornerShape(16.dp))
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = R.drawable.stethoscope),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(text = "Lakukan Rekaman Jantung", fontWeight = FontWeight.Bold)
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Atur Device", fontSize = 14.sp)
                }
            }
        }
    }
}