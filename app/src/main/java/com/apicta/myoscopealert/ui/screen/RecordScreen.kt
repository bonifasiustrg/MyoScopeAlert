package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.ui.theme.poppins
import com.apicta.myoscopealert.ui.theme.primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(navController: NavHostController) {
//    Text(text = "Record Screen")
    var title by remember {
        mutableStateOf("")
    }
    var text by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text(
                text = "Masukkan Nama File",
                color = Color.Gray,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = poppins,
                fontSize = 24.sp)
            },
            modifier = Modifier
                .background(color = Color.Transparent)
                .align(Alignment.CenterHorizontally),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,

            ),
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontFamily = poppins,

            )

        )
//        BasicTextField(
//            value = text,
//            onValueChange = {
//                text = it
//            },
//            singleLine = true,
//            decorationBox = { innerTextField ->
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
////                        .border(
////                            width = 2.dp,
////                            color = Color(0XFF4c8acc),
////                            shape = RoundedCornerShape(10.dp)
////                        )
//
//                ) {
//                    Column(
//                        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center
//                    ) {
////                        Icon(Icons.Outlined.Person, contentDescription = "")
//                        if (text.isEmpty())
//                            Text(
//                                text = "Masukkan nama file",
//                                modifier = Modifier.align(Alignment.CenterHorizontally),
//                                fontSize = 32.sp,
//                                fontWeight = FontWeight.ExtraBold,
//                                fontFamily = poppins,
//                                color = Color.Gray
//                            )
//                        // you have to invoke this function then cursor will focus and you will able to write something
//                        innerTextField.invoke()
//                    }
//                }
//            }
//        )

        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = painterResource(id = R.drawable.chart),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(primary)
                .padding(vertical = 14.dp, horizontal = 64.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Sabtu, 24-06-2023",
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(0.5f)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF72D99D)
            )
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(0.5f)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6F6F)
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pause),
                contentDescription = null,
                modifier = Modifier
                    .padding(6.dp)
                    .size(24.dp)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun RecordScreenPrev() {
    RecordScreen(rememberNavController())
}