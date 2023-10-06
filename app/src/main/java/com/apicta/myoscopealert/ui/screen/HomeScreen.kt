package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.secondary

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Hallo,")
        Text(
            text = "Sutejo Goodman,",
            fontStyle = MaterialTheme.typography.displayLarge.fontStyle,
            fontFamily = poppins,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .background(color = secondary, shape = RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 4.dp, bottom = 0.dp)
                    .size(120.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.FillHeight
            )
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp)) {
                Text(text = "Bagaimana perasaan Anda?", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text(text = "Mari mulai rekam jantung anda, untuk mengetahui kesehatan jantung anda", fontSize = 12.sp)

                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Mulai Rekam")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hasil Rekaman Terakhir",
            fontStyle = MaterialTheme.typography.labelLarge.fontStyle,
            fontWeight = FontWeight.Bold
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
                    Text(text = "28 September 2023")
                }
                Row {
                    Text(text = "oleh ")
                    Text(text = "Dokter Saparudin", fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.chart),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                    Text(text = "Negatif")
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
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Atur Device", fontSize = 14.sp)
                }
            }
        }
    }
}