package com.apicta.myoscopealert.ui.screen

import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.graphs.BottomBarScreen
import com.apicta.myoscopealert.ui.screen.common.ProcessWavFileData
import com.apicta.myoscopealert.ui.screen.common.ProcessWavFileData3
import com.apicta.myoscopealert.ui.screen.common.SetUpChart
import com.apicta.myoscopealert.ui.screen.common.ShimmerCardStatistic
import com.apicta.myoscopealert.ui.screen.common.StatusCard
import com.apicta.myoscopealert.ui.theme.MyoScopeAlertTheme
import com.apicta.myoscopealert.ui.theme.cardsecondary
import com.apicta.myoscopealert.ui.theme.greenIcon
import com.apicta.myoscopealert.ui.theme.greenIconSec
import com.apicta.myoscopealert.ui.theme.hover
import com.apicta.myoscopealert.ui.theme.orangeIcon
import com.apicta.myoscopealert.ui.theme.orangeIconSec
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary
import com.apicta.myoscopealert.ui.theme.redIcon
import com.apicta.myoscopealert.ui.theme.redIconSec
import com.apicta.myoscopealert.ui.theme.secondary
import com.apicta.myoscopealert.ui.viewmodel.DiagnosesViewModel
import com.apicta.myoscopealert.utils.calculateStatisctic
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    diagnosesViewModel: DiagnosesViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val viewModel: DiagnosesViewModel = hiltViewModel()
//    val storedToken by viewModel.userToken.collectAsState()
    val storedToken by viewModel.accountInfo.collectAsState()
    Log.e("usertoken", "$storedToken")

    runBlocking {
        viewModel.getPatientStatistic(storedToken?.token!!)
        diagnosesViewModel.diagnoseHistory()

//        viewModel.getLatestDiagnose(storedToken!!)
    }
    val statisticResponse by viewModel.patientStatisticResponse.collectAsState()
//    val latestDiagnoseResponse by viewModel.patientLatestDiagnoseResponse.collectAsState()
//    viewModel.performProfile(/*storedToken!!*/accountInfo?.token.toString())
    val historyResponse by diagnosesViewModel.diagnoseHistoryResponse.collectAsState()
    Log.e("history response", historyResponse.toString())


    val isVerified by remember {
        mutableStateOf(true)
    }
    val heartStatus by remember {
        mutableStateOf(true)
    }

    var expanded by remember { mutableStateOf(false) }
    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)

    val files = musicDir.listFiles()
    val wavFiles = files?.filter { it.extension == "wav" }
    val sortedWavFiles = wavFiles?.sortedWith(compareBy { it.lastModified() })?.lastOrNull()
    val filePath = "${musicDir.absolutePath}/${sortedWavFiles?.name}"
    Log.e("filepath", filePath)
    MyoScopeAlertTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())

        ) {
//            Text(
//                text = "Hello,",
//                modifier = modifier.padding(top = 16.dp),
//                fontWeight = FontWeight.Bold
//            )
//            Text(
//                text = "Hikmal",
//                fontWeight = FontWeight.ExtraBold,
//                fontSize = 28.sp
//            )
            Divider(
                thickness = 1.dp,
                color = hover,
                modifier = modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            val total = historyResponse?.data?.size
            val statistic = historyResponse?.let { calculateStatisctic(it, total!!) }
            Box() {
                ShimmerCardStatistic(
                    isLoading = false/*statisticResponse == null*/,
                    contentAfterLoading = {
                        Column {
                            Row(modifier.fillMaxWidth()) {
                                statistic?.let {
                                    StatusCard(
                                        it.verified/*statisticResponse!!.data.totalVerified*/,
                                        Icons.Filled.Verified,
                                        "Verified",
                                        hover,
                                        cardsecondary,
                                        modifier
                                    )
                                }
                                Spacer(modifier = modifier.width(12.dp))
                                statistic?.let {
                                    StatusCard(
                                        it.normal/*statisticResponse!!.data.totalPatientNormal*/,
                                        Icons.Filled.HealthAndSafety,
                                        "Normal",
                                        greenIcon,
                                        greenIconSec,
                                        modifier
                                    )
                                }
                            }
                            Spacer(modifier = modifier.height(12.dp))
                            Row(modifier.fillMaxWidth()) {
                                statistic?.let {
                                    StatusCard(
                                        it.unverified/*statisticResponse!!.data.totalUnverified*/,
                                        Icons.Filled.Pending,
                                        "Unverified",
                                        orangeIcon,
                                        orangeIconSec,
                                        modifier
                                    )
                                }
                                Spacer(modifier = modifier.width(12.dp))
                                statistic?.let {
                                    StatusCard(/*statisticResponse!!.data.totalPatientMi*/it.mi,
                                        Icons.Filled.Warning,
                                        "MI",
                                        redIcon,
                                        redIconSec,
                                        modifier
                                    )
                                }
                            }
                        }
                    }
                )

            }
//            if (statisticResponse != null) {
//            } else {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator()
//                }
//            }

            Spacer(modifier = modifier.height(16.dp))


//            val lastRecord = latestDiagnoseResponse?.data?.diagnoses?.lastOrNull()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val timestamp: Long = sortedWavFiles?.lastModified() ?: 10000L

            val date = Date(timestamp)
            val formattedDate = formatter.format(date)
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Recording",
                    fontSize = 20.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Show more",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = modifier.clickable {
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

                })
            }
            Spacer(modifier = modifier.height(4.dp))

//        Column(
//            modifier
//                .fillMaxWidth()
//                .clip(shape = RoundedCornerShape(16.dp))
////                .background(color = secondary)
//                .padding(16.dp)
//        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        color = secondary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = Color(0xC1FFFFFF)
                    )
                    .padding(16.dp)

            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = modifier.padding(bottom = 4.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Event,
//                        contentDescription = null,
//                        modifier = modifier
//                            .size(24.dp)
//                            .padding(end = 4.dp),
//                        tint = orangeIcon
//                    )
//                    Text(text = formattedDate/*lastRecord.createdAt*/, fontSize = 14.sp)
//                }

                if (sortedWavFiles != null) {
                    if (/*File(filePath)*/sortedWavFiles.exists()) {
//                        ProcessWavFileData3(filePath, context)
                        ProcessWavFileData(filePath, context)
                    } else {
                        SetUpChart(ctx = context)
                    }
                }

                Spacer(modifier = modifier.height(8.dp))
                Row(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Heart Health Status", fontSize = 14.sp)
                    Box(
                        modifier = modifier
                            .background(
                                color = if (heartStatus) greenIcon else redIcon,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        if (heartStatus) {
                            Text(
                                text = "Normal"/*it.diagnoses.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.getDefault()
                                    ) else it.toString()
                                }*/,
                                fontSize = 10.sp,
                                textAlign = TextAlign.End,
                                color = Color.White,
                                fontFamily = poppins
                            )
                        } else {
                            Text(
                                text = "MI"/*it.diagnoses.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.getDefault()
                                    ) else it.toString()
                                }*/,
                                fontSize = 10.sp,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = poppins
                            )
                        }
                    }


                }
                Spacer(modifier = modifier.height(4.dp))
                val annotatedString = buildAnnotatedString {
                    append("Verification status")
//                    withStyle(style = SpanStyle(color = hover, fontWeight = FontWeight.ExtraBold)) {
//                        append(" Dokter Saparudin")
//                    }
                }
                Row(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = annotatedString, fontSize = 14.sp)
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = if (isVerified) R.drawable.checkicon else R.drawable.ic_close
                        ),
                        contentDescription = null,
                        tint = greenIcon,
                        modifier = modifier.size(20.dp)
                    )
                }



                Row(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Note ", style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = poppins,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(modifier = modifier.width(4.dp))

                    if (isVerified) {
                        IconButton(
                            onClick = { expanded = !expanded },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = secondary, contentColor = Color.Black
                            )
                        ) {
                            Row {
                                Icon(
                                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                if (expanded) {
                    Text(
//                        text = "After examining the patient's heartbeat sound wave graph, I confirmed that there was no indication of Myocardial infarction. The patient's heart condition looks healthy and stable based on the graphic analysis that we have verified.",
//                        text = "Setelah memeriksa grafik gelombang suara detak jantung pasien, saya mengkonfirmasi bahwa tidak terdapat indikasi penyakit Myocardial infarction. Kondisi jantung pasien terlihat sehat dan stabil berdasarkan analisis grafik yang telah kami verifikasi.",
//                        text = it.notes,
                        text = "Tidak ada pesan dari dokter",
                        fontFamily = poppins,
                        fontSize = 14.sp

                    )
                }
//            }


            }
            Spacer(modifier = modifier.height(16.dp))

            Row(
                modifier
                    .fillMaxWidth()
                    .background(color = secondary, shape = RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.welcome),
                    contentDescription = null,
                    modifier = modifier
//                    .padding(start = 16.dp, top = 16.dp, end = 4.dp, bottom = 0.dp)
                        .size(140.dp)
                        .align(Alignment.Bottom),
                    contentScale = ContentScale.FillHeight
                )
                Column(modifier = modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp)) {
                    Text(
//                        text = "Bagaimana perasaan Anda?",
                        text = "How do you feel?",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Let's start recording your heart, to find out your heart health.",
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            navController.navigate(BottomBarScreen.Record.route)
                        },
                        colors = ButtonDefaults.buttonColors(primary),
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Text(text = "Record Heartbeat")
                    }
                }
            }
            Spacer(modifier = modifier.height(16.dp))
//
//            Row(
//                modifier
//                    .fillMaxWidth()
//                    .background(color = terniary, shape = RoundedCornerShape(16.dp))
//                    .padding(6.dp),
//                verticalAlignment = Alignment.CenterVertically
//
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.stethoscope),
//                    contentDescription = null,
//                    modifier = modifier.size(48.dp),
//                )
//                Spacer(modifier = modifier.width(16.dp))
//                Column(modifier.weight(1f)) {
//                    Text(text = "Lakukan Rekaman Jantung", fontWeight = FontWeight.Bold)
//                    Button(
//                        onClick = {
//                            navController.navigate(BottomBarScreen.Record.route)
//                        },
//                        colors = ButtonDefaults.buttonColors(primary),
//                        modifier = modifier.fillMaxWidth()
//                    ) {
//                        Text(text = "Atur Device", fontSize = 14.sp)
//                    }
//                }
//            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
private fun HomeScreenPrev() {
    MaterialTheme() {
        Column(Modifier.background(Color.White)) {

            HomeScreen(navController = rememberNavController())
        }
    }
}