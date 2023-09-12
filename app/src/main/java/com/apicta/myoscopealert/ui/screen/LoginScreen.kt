package com.apicta.myoscopealert.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.models.UserViewModel
import com.apicta.myoscopealert.ui.component.GradientButton
import com.apicta.myoscopealert.ui.component.SimpleOutlinedPasswordTextField
import com.apicta.myoscopealert.ui.component.SimpleOutlinedTextFieldSample
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, dataStoreManager: DataStoreManager) {
    // State untuk menyimpan email dan password yang dimasukkan pengguna
//    val viewModel: LoginViewModel = viewModel()
    val viewModel = hiltViewModel<UserViewModel>()
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val loginResponse by viewModel.loginResponse.collectAsState()
    val isLoginSuccess = viewModel.isLoginSuccess
    // ...
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
//            .padding(28.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    color = Color.Transparent,
                )
        ) {
            Box(
                modifier = Modifier
                    /*.background(
                        color = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(25.dp, 5.dp, 25.dp, 5.dp)
                    )*/
                    .align(Alignment.BottomCenter),
            ) {

                Image(
                    painter = painterResource(id = R.drawable.user_sign_in),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth(),

                    )
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                    ,

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    //.........................Spacer
                    Spacer(modifier = Modifier.height(50.dp))

                    //.........................Text: title
                    Text(
                        text = "Sign In",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 130.dp)
                            .fillMaxWidth( ),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SimpleOutlinedTextFieldSample(emailState)

                    Spacer(modifier = Modifier.padding(3.dp))
                    SimpleOutlinedPasswordTextField(passwordState)

                    val gradientColor = listOf(Color(0xFF484BF1), Color(0xFF673AB7))
                    val cornerRadius = 16.dp


                    Spacer(modifier = Modifier.padding(10.dp))
                    GradientButton(
                        gradientColors = gradientColor,
                        cornerRadius = cornerRadius,
                        nameButton = "Login",
                        roundedCornerShape = RoundedCornerShape(topStart = 30.dp,bottomEnd = 30.dp),
                        onClick = {
                            if (emailState.value.isNotEmpty() && passwordState.value.isNotEmpty()) {
                                viewModel.performLogin(emailState.value, passwordState.value)
                                Log.e("login", "${viewModel.isLoginSuccess.value}} dan ${viewModel.loginResponse.value}")


                                // Tambahkan penundaan selama misalnya 1 detik sebelum navigasi
                                viewModel.viewModelScope.launch {
                                    delay(1000) // Penundaan selama 1 detik (1000 ms)
                                    navController.navigate("dashboard")
                                    Log.e("login", "navigate to dashboard")
                                }
//                                viewModel.viewModelScope.launch {
//                                    val loginJob = async(Dispatchers.IO) {
//                                        viewModel.performLogin(emailState.value, passwordState.value)
//                                    }
//                                    loginJob.await() // Wait for the login job to complete
//                                    delay(4000) // Delay for 1 second (1000 ms
//                                    val storedToken = runBlocking { dataStoreManager.getAuthToken.first()
//                                    }
//
//
//                                    if (/*loginJob.isCompleted &&*/ storedToken?.isNotBlank() == true) {
//                                        Log.e("login tes", "token stored $storedToken")
//                                        // Navigasi ke Dashboard hanya jika login berhasil
//                                        navController.navigate("dashboard")
//                                    } else {
//                                        Log.e("login", "Login failed")
//                                    }
//                                }
                            } else {
                                Log.e("login", "Try Again, email and password are empty")
                            }
                            passwordState.value = ""
                            emailState.value = ""
                        }
                    )
                    Spacer(modifier = Modifier.height(200.dp))
                }


            }

        }
    }
}

//@Preview
//@Composable
//fun LoginPreview() {
//    LoginScreen(navController = rememberNavController(), dataStoreManager = DataStoreManager.getInstance(LocalContext.current))
//}