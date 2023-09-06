package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.models.LoginViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    // State untuk menyimpan email dan password yang dimasukkan pengguna
//    val viewModel: LoginViewModel = viewModel()
    val viewModel = hiltViewModel<LoginViewModel>()
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    // ...
    Column {

        // TextField untuk email
        TextField(
            value = emailState.value,
            onValueChange = { newValue ->
                emailState.value = newValue
            },
            label = { Text("Email") }
        )

        // TextField untuk password
        TextField(
            value = passwordState.value,
            onValueChange = { newValue ->
                passwordState.value = newValue
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        // ...

        // Tombol login
        Button(
            onClick = {
                val email = emailState.value
                val password = passwordState.value
                viewModel.performLogin(email, password)
                navController.navigate("dashboard")

            }
        ) {
            Text("Login")
        }
    }
}
