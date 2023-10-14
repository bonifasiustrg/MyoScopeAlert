package com.apicta.myoscopealert.graphs

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.ui.screen.DashboardScreen
import com.apicta.myoscopealert.ui.screen.HomeScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


@Composable
fun RootNavigationGraph(navController: NavHostController, dataStoreManager: DataStoreManager) {
    val storedToken = runBlocking { dataStoreManager.getAuthToken.first() }
    Log.e("token app navigation", storedToken.toString())

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = /*Graph.AUTHENTICATION*/
        if (!storedToken.isNullOrEmpty()) {
            Graph.MAIN
        } else {
            Graph.AUTHENTICATION
        },

    ) {
        authNavGraph(navController = navController, dataStoreManager = dataStoreManager)
        composable(route = Graph.MAIN) {
//            HomeScreen()
            DashboardScreen(dataStoreManager = dataStoreManager)
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
//    const val HOME = "home_graph"
    const val MAIN = "main_graph"
//    const val DETAILS = "details_graph"
//    new other graph
}