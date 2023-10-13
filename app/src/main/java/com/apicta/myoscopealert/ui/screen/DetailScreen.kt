package com.apicta.myoscopealert.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.ui.theme.primary

@Composable
fun HistoryDetail(navController: NavHostController) {
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Ceritanya ini nama file", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = "Dokter : Saparudin ", color = Color.White)
        Text(text = "Tanggal : 9 September 2023", color = Color.White)

        Spacer(modifier = Modifier.height(48.dp))

        Text(text = "Hasil Rekaman")

        Image(
            painter = painterResource(id = R.drawable.chart),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Status")

        Box(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color(0xFFFF6F6F))
                .padding(vertical = 14.dp, horizontal = 64.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8f)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "MI",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )

                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color(0xFF72D99D))
                .padding(vertical = 14.dp, horizontal = 64.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8f)

        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "MI",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )

                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(BottomBarScreen.History.route) {
                    // Pop up to the start destination of the graph
                    popUpTo(navController.graph.startDestinationId) {
                        // Pop all inclusive
                        inclusive = true
                    }
                    // Avoid multiple copies of the same destination when re-selecting it
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }

            },
            colors = ButtonDefaults.buttonColors(primary),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Selesai")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryDetailPrev() {
    HistoryDetail(rememberNavController())
}