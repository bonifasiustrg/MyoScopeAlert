@file:OptIn(ExperimentalMaterial3Api::class)

package com.apicta.myoscopealert.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.models.DiagnosesViewModel
import com.apicta.myoscopealert.models.UserViewModel
import com.apicta.myoscopealert.ui.component.MainTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun DashboardScreen(navController: NavHostController, dataStoreManager: DataStoreManager) {
    var storedToken by remember { mutableStateOf<String?>(null) }
    Log.d("DashboardScreen1", "Stored Token: $storedToken")

    // Ambil token jika belum diinisialisasi
    if (storedToken == null) {
        runBlocking {
            storedToken = dataStoreManager.getAuthToken.first()
            Log.d("DashboardScreen runblocking", "Stored Token: $storedToken")
        }
    }

    storedToken?.let { Log.e("stored token dashboard", it) }
    val viewModel = hiltViewModel<DiagnosesViewModel>()
    val viewModelUser = hiltViewModel<UserViewModel>()
    Log.e("dashboard", "viewmodel init")
    viewModel.performDiagnoses(storedToken!!)

    val diagnosesResponse by viewModel.diagnosesResponse.collectAsState()
    Log.e("diagnosesResponse", diagnosesResponse.toString())


    Scaffold(
        topBar = { MainTopBar("Dashboard", navController = navController) }
    ) {
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(16.dp)) {
            Column(Modifier.fillMaxSize()) {
//                Text(text = "Dashboard Screen", fontWeight = FontWeight.Bold)
                if (diagnosesResponse == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Text(text = "Your Data", fontWeight = FontWeight.Bold)
                    Text(text = "${diagnosesResponse!!.message}", fontWeight = FontWeight.Bold, color = Color.Green)

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = "Doctor Profile", fontWeight = FontWeight.Bold)
                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.fullname}")
                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.id}")
                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.age}")
                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.address}")
                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.emergencyPhone}")
                    Text(text = "${diagnosesResponse!!.data?.get(0)?.doctor?.gender}")


                    val diagnoses = diagnosesResponse!!.data?.get(0)?.diagnoses
                    val isVerified = diagnosesResponse!!.data?.get(0)?.isVerified
                    Text(text = "Diagnoses", fontWeight = FontWeight.Bold)
                    Text(text = "$diagnoses", modifier = Modifier.background(color = if (diagnoses == "normal") Color.Green else Color.Red))
                    Text(text = "${diagnosesResponse!!.data?.get(0)?.id}")
                    Text(text = "${diagnosesResponse!!.data?.get(0)?.notes}")
                    Text(text = "${diagnosesResponse!!.data?.get(0)?.file}")
                    Text(text = if (isVerified == "0") "Not Verified" else "Verified")
                    Text(text = "${diagnosesResponse!!.data?.get(0)?.updatedAt}")
//                    Text(text = "${diagnosesResponse!!.data?.get(0)?.patient?.condition}")





//                    Button(
//                        onClick = {
//                            if (storedToken != null) {
////                                viewModelUser.performProfile(storedToken!!)
////                            Log.e("login", "${viewModel.isLoginSuccess.value}} dan ${viewModel.loginResponse.value}")
//
//
//                                // Tambahkan penundaan selama misalnya 1 detik sebelum navigasi
//                                viewModelUser.viewModelScope.launch {
//                                    delay(500) // Penundaan selama 1 detik (1000 ms)
//                                    navController.navigate("profile_screen")
//                                    Log.e("dashboard", "navigate to profile")
//                                }
//                            } else {
//                                Log.e("dashboard to login", "can not to profile")
//                            }
//                        }
//                    ) {
//                        Text("Profile")
//                    }
                }
            }
        }
    }
}