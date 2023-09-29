package com.apicta.myoscopealert

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.ui.screen.DashboardScreen
import com.apicta.myoscopealert.ui.screen.HistoryScreen
import com.apicta.myoscopealert.ui.screen.LoginScreen
import com.apicta.myoscopealert.ui.screen.ProfileScreen
import com.apicta.myoscopealert.ui.theme.MyoScopeAlertTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()


        setContent {
            MyoScopeAlertTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /*INSTANCE*/
//                    MqttClientManager() // nstance of MqttClientManager

//                    val context = LocalContext.current
//                    val dataStoreManager = DataStoreManager.getInstance(applicationContext)
//                    val storedToken = runBlocking { dataStoreManager.getAuthToken.first() }
//                    val apiService = Retro.getRetroClientInstance().create(UserApi::class.java)
//
//                    val signInRequest = SignInRequest()
//                    signInRequest.email = "admin@mail.com"
//                    signInRequest.password = "password"
//
//                    // Menggunakan coroutines untuk menjalankan pemanggilan API secara asynchronous
//                    val scope = rememberCoroutineScope()
//                    scope.launch {
//                        try {
//                            val response: Response<SignInResponse> = withContext(Dispatchers.IO) {
//                                apiService.login(signInRequest)
//                            }
//                            if (response.isSuccessful) {
////                                val rawResponse: String? = response.body()?.string()
//                                val rawResponse = response.body()
//                                if (rawResponse != null) {
//
////                                     Lakukan sesuatu dengan rawResponse (data mentah)
//                                    Log.e("status", "Login Successfully")
//                                    Log.e("token", rawResponse.data?.token ?: "Token not available")
//                                    val token = rawResponse.data?.token ?: ""
//                                    GlobalScope.launch {
//                                        dataStoreManager.setUserToken(token)
//                                    }
//
//                                    Log.e("SignInActivity", "$rawResponse")
//                                } else {
//                                    Log.e("SignInActivity", "Response body is null")
//                                }
//                            } else {
//                                Log.e("SignInActivity", "Request failed with code ${response.code()}")
//                                // Tangani respon gagal
//                            }
//                        } catch (e: Exception) {
//                            Log.e("SignInActivity", "Error during API call: ${e.message}", e)
//                            // Tangani exception, misalnya jaringan bermasalah
//                        }
//                    }
                    val navController = rememberNavController()


                    AppNavigation(navController, dataStoreManager)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, dataStoreManager: DataStoreManager) {
    val startDestination: String
    val storedToken = runBlocking { dataStoreManager.getAuthToken.first() }
    Log.e("token app navigation", storedToken.toString())
    startDestination = if (!storedToken.isNullOrEmpty()) {
        "dashboard"
    } else {
        "login_screen"
    }
//    val startDestination = "login_screen"
    NavHost(navController = navController, startDestination = startDestination, builder = {
//        composable("welcome_screen", content = { WelcomeScreen(navController = navController) })
        composable("login_screen", content = { LoginScreen(navController = navController, dataStoreManager) })
//        composable("register_screen", content = { RegisterScreen(navController = navController) })
        composable("dashboard", content = { DashboardScreen(navController = navController, dataStoreManager) })
        composable("profile_screen", content = { ProfileScreen(navController = navController, dataStoreManager) })
        composable("history_screen", content = { HistoryScreen(navController = navController, storedToken!!) })
    })
}
