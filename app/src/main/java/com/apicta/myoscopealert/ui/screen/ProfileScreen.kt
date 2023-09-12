package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.models.UserViewModel

@Composable
fun ProfileScreen(navController: NavHostController, storedToken: String) {

    val viewModel = hiltViewModel<UserViewModel>()
    viewModel.performProfile(storedToken)
    val profileResponse by viewModel.profileResponse.collectAsState()


    Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            Text(text = "Profile Screen")

            Text(text = profileResponse?.data?.user?.id.toString())
            Text(text = profileResponse?.data?.user?.email.toString())



            Button(colors = ButtonDefaults.buttonColors(Color.Red),
                onClick = {
                    viewModel.performLogout(storedToken)
                    navController.navigate("login_screen")

                }
            ) {
                Text("Logout")
            }
        }
    }
}