package com.apicta.myoscopealert.graphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.ui.screen.ConnectBluetoothScreen
import com.apicta.myoscopealert.ui.screen.FileDetail
import com.apicta.myoscopealert.ui.screen.HistoryScreen
import com.apicta.myoscopealert.ui.screen.HomeScreen
import com.apicta.myoscopealert.ui.screen.ProfileScreen
import com.apicta.myoscopealert.ui.screen.RecordScreen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainNavGraph(navController: NavHostController, dataStoreManager: DataStoreManager) {
    val storedToken = "token"
    NavHost(
        navController = navController,
        route = Graph.MAIN,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(route = BottomBarScreen.Record.route) {
            RecordScreen(navController = navController)
        }
        composable(route = BottomBarScreen.History.route) {
            HistoryScreen(navController = navController, storedToken)
//            HistoryScreen(navController)
//            FileListScreen(navController)
        }
        composable(route = BottomBarScreen.Profile.route) {
            ProfileScreen(navController = navController, dataStoreManager)
        }

//        composable(route = "detail_history") {
//            HistoryDetail(navController)
//        }
        composable(route = "connect_bluetooth") {
            ConnectBluetoothScreen()
        }

        composable(
            route = "detail/{fileName}/{date}",
            arguments = listOf(
                navArgument("fileName") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            ),

//            enterTransition = {
//                slideIntoContainer(
//                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
//                    animationSpec = tween(700)
//                )
//            },
//            exitTransition = {
//                slideOutOfContainer(
//                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
//                    animationSpec = tween(700)
//                )
//            },
//            popEnterTransition = {
//                slideIntoContainer(
//                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
//                    animationSpec = tween(700)
//                )
//            },
//            popExitTransition = {
//                slideOutOfContainer(
//                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
//                    animationSpec = tween(700)
//                )
//            }
        ) { backStackEntry ->
            // Dapatkan nilai fileName dari argumen navigasi
            val fileName = backStackEntry.arguments?.getString("fileName")
            val fileDate = backStackEntry.arguments?.getString("date")

            // Komponen untuk menampilkan halaman detail
            // Di dalam komponen ini, Anda dapat menggunakan `fileName` untuk menampilkan konten yang sesuai
            FileDetail(fileName, fileDate, dataStoreManager, navController)
        }

//        detailsNavGraph(navController = navController)
    }
}

//fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
//    navigation(
//        route = Graph.DETAILS,
//        startDestination = DetailsScreen.Information.route
//    ) {
//        composable(route = DetailsScreen.Information.route) {
//            ScreenContent(name = DetailsScreen.Information.route) {
//                navController.navigate(DetailsScreen.Overview.route)
//            }
//        }
//        composable(route = DetailsScreen.Overview.route) {
//            ScreenContent(name = DetailsScreen.Overview.route) {
//                navController.popBackStack(
//                    route = DetailsScreen.Information.route,
//                    inclusive = false
//                )
//            }
//        }
//    }
//}

//sealed class DetailsScreen(val route: String) {
//    object Information : DetailsScreen(route = "INFORMATION")
//    object Overview : DetailsScreen(route = "OVERVIEW")
//}
