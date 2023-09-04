package com.apicta.myoscopealert

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.data.login.SignInRequest
import com.apicta.myoscopealert.data.login.SignInResponse
import com.apicta.myoscopealert.network.Retro
import com.apicta.myoscopealert.network.UserApi
import com.apicta.myoscopealert.ui.theme.MyoScopeAlertTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

class MainActivity : ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyoScopeAlertTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /*INSTANCE*/
                    MqttClientManager() // nstance of MqttClientManager

                    val context = LocalContext.current
                    val dataStoreManager = DataStoreManager.getInstance(applicationContext)
                    val storedToken = runBlocking { dataStoreManager.getAuthToken.first() }
                    val apiService = Retro.getRetroClientInstance().create(UserApi::class.java)

                    val signInRequest = SignInRequest()
                    signInRequest.email = "admin@mail.com"
                    signInRequest.password = "password"

                    // Menggunakan coroutines untuk menjalankan pemanggilan API secara asynchronous
                    val scope = rememberCoroutineScope()
                    scope.launch {
                        try {
                            val response: Response<SignInResponse> = withContext(Dispatchers.IO) {
                                apiService.login(signInRequest)
                            }
                            if (response.isSuccessful) {
//                                val rawResponse: String? = response.body()?.string()
                                val rawResponse = response.body()
                                if (rawResponse != null) {

//                                     Lakukan sesuatu dengan rawResponse (data mentah)
                                    Log.e("status", "Login Successfully")
                                    Log.e("token", rawResponse.data?.token ?: "Token not available")
                                    val token = rawResponse.data?.token ?: ""
                                    GlobalScope.launch {
                                        dataStoreManager.setUserToken(token)
                                    }

                                    Log.e("SignInActivity", "$rawResponse")
                                } else {
                                    Log.e("SignInActivity", "Response body is null")
                                }
                            } else {
                                Log.e("SignInActivity", "Request failed with code ${response.code()}")
                                // Tangani respon gagal
                            }
                        } catch (e: Exception) {
                            Log.e("SignInActivity", "Error during API call: ${e.message}", e)
                            // Tangani exception, misalnya jaringan bermasalah
                        }
                    }
                }
            }
        }
    }
}
