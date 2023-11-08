package com.apicta.myoscopealert.ui.screen

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.bluetooth.BTAppActivity
import com.apicta.myoscopealert.bluetooth.BluetoothActivity
import com.apicta.myoscopealert.bluetooth.ConnectActivity
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.ui.theme.cardbg
import com.apicta.myoscopealert.ui.theme.greenIcon
import com.apicta.myoscopealert.ui.theme.orangeIcon
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.redIcon
import com.apicta.myoscopealert.ui.theme.secondary
import com.apicta.myoscopealert.ui.theme.terniary
import dagger.hilt.android.qualifiers.ApplicationContext

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current

    val isVerified by remember {
        mutableStateOf(true)
    }
    val heartStatus by remember {
        mutableStateOf(true)
    }

    var expanded by remember { mutableStateOf(false) }

    val filePath =
        "/storage/emulated/0/Android/data/com.apicta.myoscopealert/files/Recordings/recordwave1.wav"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())

    ) {
        Text(text = "Hallo,", modifier = Modifier.padding(top = 16.dp))
        Text(
            text = "Eugene Wehner,",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .background(color = secondary, shape = RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = null,
                modifier = Modifier
//                    .padding(start = 16.dp, top = 16.dp, end = 4.dp, bottom = 0.dp)
                    .size(160.dp)
                    .align(Alignment.Bottom),
                contentScale = ContentScale.FillHeight
            )

            Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp)) {
                Text(
                    text = "Bagaimana perasaan Anda?",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
                Text(
                    text = "Mari mulai rekam jantung anda, untuk mengetahui kesehatan jantung anda",
                    fontSize = 12.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        navController.navigate(BottomBarScreen.Record.route)
                    },
                    colors = ButtonDefaults.buttonColors(primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Mulai Rekam")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Rekaman Terakhir",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))

//        Column(
//            Modifier
//                .fillMaxWidth()
//                .clip(shape = RoundedCornerShape(16.dp))
////                .background(color = secondary)
//                .padding(16.dp)
//        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = secondary,
                    shape = RoundedCornerShape(16.dp))
                .background(
                    color = Color(0xC1FFFFFF)
                )
                .padding(16.dp)

        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp),
                    tint = orangeIcon
                )
                Text(text = "30 Oktober 2023")
            }
//                Row {
//                    Text(text = "oleh ")
//                    Text(text = "Dokter Saparudin", fontWeight = FontWeight.ExtraBold)
//                }

            SetUpChart(ctx = context)
//                ProcessWavFileData(filePath, context)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Kesehatan Jantung")
                Box(
                    modifier = Modifier
                        .background(
                            color = if (heartStatus) greenIcon else redIcon,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    if (heartStatus) {
                        Text(
                            text = "Normal",
                            fontSize = 10.sp,
                            textAlign = TextAlign.End,
                            color = Color.White,
                            fontFamily = poppins
                        )
                    }  else {
                        Text(
                            text = "MI",
                            fontSize = 10.sp,
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = poppins

                        )
                    }
                }



            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Verified by Dokter Saparudin")
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.checkicon /*else R.drawable.ic_close*/),
                    contentDescription = null,
                    tint = greenIcon,
                    modifier = Modifier.size(20.dp)
                )
            }



            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Catatan ", style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = poppins
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))

                if(isVerified) {
                    IconButton(onClick = { expanded = !expanded }, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = secondary, contentColor = Color.Black
                    )) {
                        Row {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (expanded) {
                Text(
                    text = "Setelah memeriksa grafik gelombang suara detak jantung pasien, saya mengkonfirmasi bahwa tidak terdapat indikasi penyakit Myocardial infarction. Kondisi jantung pasien terlihat sehat dan stabil berdasarkan analisis grafik yang telah kami verifikasi.",
                    fontFamily = poppins

                )
            }
//            }



        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .background(color = terniary, shape = RoundedCornerShape(16.dp))
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = R.drawable.stethoscope),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(text = "Lakukan Rekaman Jantung", fontWeight = FontWeight.Bold)
                Button(
                    onClick = {
//                              navController.navigate("connect_bluetooth")
                        context.startActivity(Intent(context, BTAppActivity::class.java))

                    },
                    colors = ButtonDefaults.buttonColors(primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Atur Device", fontSize = 14.sp)
                }
            }
        }
    }
}