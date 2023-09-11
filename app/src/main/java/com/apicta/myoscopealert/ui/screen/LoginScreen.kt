package com.apicta.myoscopealert.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.models.UserViewModel


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
            .padding(28.dp)
    ) {
        Column {

            // TextField untuk email
            TextField(
                value = emailState.value,
                onValueChange = { newValue ->
                    emailState.value = newValue
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().clip(
                    RoundedCornerShape(8.dp)
                )
            )

            // TextField untuk password
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = passwordState.value,
                onValueChange = { newValue ->
                    passwordState.value = newValue
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().clip(
                    RoundedCornerShape(8.dp))
            )

            // ...

            // Tombol login
            Button(modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
                onClick = {

                    if (emailState.value.isNotEmpty() && passwordState.value.isNotEmpty()) {
                        viewModel.performLogin(emailState.value, passwordState.value)
                        Log.e("login", "${viewModel.isLoginSuccess.value}} dan ${viewModel.loginResponse.value}")

                        navController.navigate("dashboard")
                    } else {
                        Log.e("login", "Try Again, email and password are empty")
                    }
                    passwordState.value = ""
                    emailState.value = ""

                }
            ) {
                Text("Login")
            }

//            Button(
//                onClick = {
//                    navController.navigate("login_screen")
//                }
//            ) {
//                Text("Clear datastore")
//            }
        }
    }
}
