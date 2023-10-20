package com.apicta.myoscopealert.ui.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current

    val filePath =
        "/storage/emulated/0/Android/data/com.apicta.myoscopealert/files/Recordings/Record17Oct.wav"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())

    ) {
        Text(text = "Hallo,", modifier = Modifier.padding(top = 16.dp))
        Text(
            text = "Oorlo Moore,",
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
                Text(text = "Bagaimana perasaan Anda?", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text(text = "Mari mulai rekam jantung anda, untuk mengetahui kesehatan jantung anda", fontSize = 12.sp, lineHeight = 20.sp)
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
            text = "Hasil Rekaman Terakhir",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(16.dp))
                .background(color = secondary)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xC1FFFFFF),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)

            ) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_time),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp),
                        tint = Color.Yellow
                    )
                    Text(text = "15 Oktober 2023")
                }
                Row {
                    Text(text = "oleh ")
                    Text(text = "Dokter Saparudin", fontWeight = FontWeight.ExtraBold)
                }

//                Image(
//                    painter = painterResource(id = R.drawable.chart),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    contentScale = ContentScale.FillWidth
//                )
                SetUpChart(ctx = context)
//                ProcessWavFileData(filePath, context)

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Myocardial Infarction")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp),
                        tint = Color.Red
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Normal")
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green
                    )
                }
            }


        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .background(color = Color.Transparent, shape = RoundedCornerShape(16.dp))
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
                              navController.navigate("connect_bluetooth")
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