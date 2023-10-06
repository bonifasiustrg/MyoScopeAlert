package com.apicta.myoscopealert.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.ui.screen.DashboardScreen
import com.apicta.myoscopealert.ui.screen.HistoryScreen
import com.apicta.myoscopealert.ui.screen.HomeScreen
import com.apicta.myoscopealert.ui.screen.ProfileScreen
import com.apicta.myoscopealert.ui.screen.RecordScreen

@Composable
fun HomeNavGraph(navController: NavHostController, dataStoreManager: DataStoreManager) {
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
            HistoryScreen(navController = navController, storedToken!!)
        }


        composable(route = BottomBarScreen.Profile.route) {
            ProfileScreen(navController = navController, dataStoreManager)
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
