package com.apicta.myoscopealert.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.ui.screen.auth.LoginScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController, dataStoreManager: DataStoreManager) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Login.route
    ) {
        composable(route = AuthScreen.Login.route) {
            LoginScreen(navController = navController, dataStoreManager = dataStoreManager)
        }
//        composable(route = AuthScreen.SignUp.route) {
//            ScreenContent(name = AuthScreen.SignUp.route) {}
//        }
//        composable(route = AuthScreen.Forgot.route) {
//            ScreenContent(name = AuthScreen.Forgot.route) {}
//        }
    }
}

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen(route = "LOGIN")
//    object SignUp : AuthScreen(route = "SIGN_UP")
//    object Forgot : AuthScreen(route = "FORGOT")
}