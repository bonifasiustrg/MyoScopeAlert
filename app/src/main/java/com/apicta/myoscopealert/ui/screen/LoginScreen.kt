package com.apicta.myoscopealert.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.models.UserViewModel
import com.apicta.myoscopealert.ui.common.GradientButton
import com.apicta.myoscopealert.ui.common.SimpleOutlinedPasswordTextField
import com.apicta.myoscopealert.ui.common.SimpleOutlinedTextFieldSample
import com.apicta.myoscopealert.ui.theme.poppins
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, dataStoreManager: DataStoreManager) {
    // State untuk menyimpan email dan password yang dimasukkan pengguna
//    val viewModel: LoginViewModel = viewModel()
    val viewModel = hiltViewModel<UserViewModel>()
    val tokenSaved by viewModel.tokenSaved.collectAsState()

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val loginResponse by viewModel.loginResponse.collectAsState()
    val isLoginSuccess = viewModel.isLoginSuccess
    val isLoading = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // ...
    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
        ) {
//            Image(
//                painter = painterResource(id = R.drawable.user_sign_in),
//                contentDescription = null,
//                contentScale = ContentScale.Fit,
//                modifier = Modifier
//                    .height(180.dp)
//                    .fillMaxWidth(),
//
//                )
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF293077),
                        shape = RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp)
                    )

                    .align(Alignment.BottomCenter),
            ) {

                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
//                        .background(color = Color(0xFF293077))
//                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    Text(
                        text = "Masuk",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontFamily = poppins,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Email",
                        fontFamily = poppins,
                        fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Start)
                            .padding(bottom = 4.dp)
                    )
                    SimpleOutlinedTextFieldSample(emailState)

                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "Password",
                        fontFamily = poppins,
                        fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Start)
                            .padding(bottom = 4.dp)
                    )
                    SimpleOutlinedPasswordTextField(passwordState)

//                    val gradientColor = listOf(Color(0xFF484BF1), Color(0xFF673AB7))
                    val gradientColor = listOf(Color(0xFF4CAF50), Color(0xFF009688))


                    Spacer(modifier = Modifier.padding(14.dp))
                    GradientButton(
                        gradientColors = gradientColor,
                        cornerRadius = 16.dp,
                        nameButton = "LOGIN",
                        roundedCornerShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                        onClick = {
                            if (emailState.value.isNotEmpty() && passwordState.value.isNotEmpty()) {
                                isLoading.value = true
                                viewModel.performLogin(emailState.value, passwordState.value)
                                Log.e(
                                    "login",
                                    "${viewModel.isLoginSuccess.value}} dan ${viewModel.loginResponse.value}"
                                )
                                scope.launch {
                                    viewModel.tokenSaved.collect { tokenSaved ->
                                        if (tokenSaved) {
                                            navController.navigate("dashboard_screen")
                                            // Token sudah disimpan, navigasi ke DashboardScreen
                                            Log.e(
                                                "login",
                                                "Token sudah disimpan, navigasi ke DashboardScreen"
                                            )
                                            Log.e("login", "navigate to dashboard")
                                        } else {
                                            Log.e(
                                                "login",
                                                "Token belum disimpan, mungkin tampilkan loading atau pesan lainnya"
                                            )
                                            // Token belum disimpan, mungkin tampilkan loading atau pesan lainnya
                                        }
                                    }
                                }
                            } else {
                                Log.e("login", "Try Again, email and password are empty")
                            }
                            passwordState.value = ""
                            emailState.value = ""
                        },
                        isLoading = isLoading
                    )

//                    CircularProgressIndicator(modifier = Modifier.height(18.dp), color = Color.Yellow)
                    Spacer(modifier = Modifier.padding(70.dp))
//                    if (isLoading.value){
//                        CircularProgressIndicator(modifier = Modifier.height(18.dp))
//                        Spacer(modifier = Modifier.padding(52.dp))
//                    } else {
//                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen(
        navController = rememberNavController(),
        dataStoreManager = DataStoreManager.getInstance(LocalContext.current)
    )
}