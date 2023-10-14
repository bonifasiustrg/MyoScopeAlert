package com.apicta.myoscopealert.ui.screen

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.MainActivity
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.models.UserViewModel
import com.apicta.myoscopealert.ui.common.AnimatedPreloader
import com.apicta.myoscopealert.ui.theme.primary
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ProfileScreen(navController: NavHostController, dataStoreManager: DataStoreManager) {
    val context = LocalContext.current
    var storedToken by remember { mutableStateOf<String?>(null) }
    Log.d("ProfileScreen1", "Stored Token: $storedToken")
    val scope = rememberCoroutineScope()

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


//    Surface {
//        Box(modifier = Modifier
//            .fillMaxWidth()
//            .background(color = primary)
//            .height(200.dp))
//    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                color = primary,
                shape = RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp)
            )
    )


    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp)

    ) {

        Text(text = "Profile Screen", fontWeight = FontWeight.Bold)
        if (profileResponse == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
//                CircularProgressIndicator()
                Box(modifier = Modifier.size(32.dp)) {
                    AnimatedPreloader()
                }
            }
        } else {

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = profileResponse?.data?.profile?.fullname.toString(), fontWeight = FontWeight.Bold, fontSize = 36.sp)
            Text(text = profileResponse?.data?.user?.email.toString(), color = Color.White)

            Image(
                painter = painterResource(id = R.drawable.profile),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .align(CenterHorizontally)
                    .size(88.dp),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Address", fontWeight = FontWeight.Bold)
            Text(text = profileResponse?.data?.profile?.address.toString())

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Phones", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = profileResponse?.data?.profile?.phone.toString())

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Age", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = profileResponse?.data?.profile?.age.toString())

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Gender", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = profileResponse?.data?.profile?.gender.toString())

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Condition", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = profileResponse?.data?.profile?.condition.toString())

//            Text(text = profileResponse?.data?.user?.id.toString(), color = Color.White)
//            Text(text = profileResponse?.data?.profile?.emergencyPhone.toString())

//            Spacer(modifier = Modifier.height(16.dp))
//            Text(text = "Relations", fontWeight = FontWeight.Bold)
//            Text(text = profileResponse?.data?.profile?.relations?.get(0)?.fullname.toString())
//            Text(text = profileResponse?.data?.profile?.relations?.get(0)?.address.toString())
//            Text(text = profileResponse?.data?.profile?.relations?.get(0)?.phone.toString())
//            Text(text = profileResponse?.data?.profile?.relations?.get(0)?.emergencyPhone.toString())
//            Text(text = profileResponse?.data?.profile?.relations?.get(0)?.gender.toString())
//            Text(text = profileResponse?.data?.profile?.relations?.get(0)?.age.toString())



//            Spacer(modifier = Modifier.height(16.dp))

        }
        Spacer(modifier = Modifier.weight(1f))
        Button(colors = ButtonDefaults.buttonColors(Color.Red),
            modifier = Modifier.align(CenterHorizontally),
            onClick = {
                isLoading.value = true
                viewModel.performLogout(storedToken!!)
                scope.launch {
                    delay(1000)
                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(intent)
                }


            }
        ) {
            if (isLoading.value) {
//                CircularProgressIndicator(color = Color.Yellow)
                AnimatedPreloader()
            } else {
                Text("Logout")
                Icon(imageVector = Icons.Default.Logout, contentDescription = null)
            }
        }
    }

}

@Preview
@Composable
fun ProfilePrev() {
    ProfileScreen(
        navController = rememberNavController(), dataStoreManager = DataStoreManager(
            LocalContext.current
        )
    )
}