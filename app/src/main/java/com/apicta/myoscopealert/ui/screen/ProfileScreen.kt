package com.apicta.myoscopealert.ui.screen

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.MainActivity
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.models.UserViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun ProfileScreen(navController: NavHostController, dataStoreManager: DataStoreManager) {
    val context = LocalContext.current
    var storedToken by remember { mutableStateOf<String?>(null) }
    Log.d("ProfileScreen1", "Stored Token: $storedToken")

    val isLoading = remember { mutableStateOf(false) }
    // Ambil token jika belum diinisialisasi
    if (storedToken == null) {
        runBlocking {
            storedToken = dataStoreManager.getAuthToken.first()
            Log.d("ProfileScreen runblocking", "Stored Token: $storedToken")
        }
    }


    val viewModel = hiltViewModel<UserViewModel>()
    viewModel.performProfile(storedToken!!)
    val profileResponse by viewModel.profileResponse.collectAsState()


    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Column(Modifier.fillMaxSize()) {
            Text(text = "Profile Screen", fontWeight = FontWeight.Bold)
            if (profileResponse == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center){
                    CircularProgressIndicator()
                }
            } else {

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "Your Data", fontWeight = FontWeight.Bold)
                Text(text = profileResponse?.data?.user?.id.toString())
                Text(text = profileResponse?.data?.user?.email.toString())

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = profileResponse?.data?.profile?.fullname.toString())
                Text(text = profileResponse?.data?.profile?.address.toString())
                Text(text = profileResponse?.data?.profile?.phone.toString())
                Text(text = profileResponse?.data?.profile?.emergencyPhone.toString())
                Text(text = profileResponse?.data?.profile?.age.toString())
                Text(text = profileResponse?.data?.profile?.gender.toString())
                Text(text = profileResponse?.data?.profile?.condition.toString())

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Relations", fontWeight = FontWeight.Bold)
                Text(text = profileResponse?.data?.profile?.relations?.get(0)?.fullname.toString())
                Text(text = profileResponse?.data?.profile?.relations?.get(0)?.address.toString())
                Text(text = profileResponse?.data?.profile?.relations?.get(0)?.phone.toString())
                Text(text = profileResponse?.data?.profile?.relations?.get(0)?.emergencyPhone.toString())
                Text(text = profileResponse?.data?.profile?.relations?.get(0)?.gender.toString())
                Text(text = profileResponse?.data?.profile?.relations?.get(0)?.age.toString())



                Spacer(modifier = Modifier.height(16.dp))

            }
            Button(colors = ButtonDefaults.buttonColors(Color.Red),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    isLoading.value = true
                    viewModel.performLogout(storedToken!!)
                    GlobalScope.launch {
                        delay(1000)
                        val intent = Intent(context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context.startActivity(intent)
                    }


                }
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(color = Color.Yellow)
                } else {
                    Text("Logout")
                }
            }
        }
    }
}