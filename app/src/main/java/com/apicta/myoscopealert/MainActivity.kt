package com.apicta.myoscopealert

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.graphs.RootNavigationGraph
import com.apicta.myoscopealert.ui.theme.MyoScopeAlertTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStoreManager: DataStoreManager




    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        installSplashScreen()
        // Check to see if the Bluetooth classic feature is available.
        val bluetoothAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)

        // Check to see if the BLE feature is available.
        val bluetoothLEAvailable =
            packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)

        setContent {
            MyoScopeAlertTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /*INSTANCE*/
                    Log.e("bt", "$bluetoothAvailable $bluetoothLEAvailable")
//                    Button(onClick = {
//                        takePermission.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
//
//                    }) {
//                        Text("Konek Bluetooth")
//                    }
                    RootNavigationGraph(navController = rememberNavController(), dataStoreManager)


                }
            }
        }
    }
}
