package com.apicta.myoscopealert.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.apicta.myoscopealert.R
import com.apicta.myoscopealert.ui.GradientButton
import com.apicta.myoscopealert.ui.SimpleOutlinedPasswordTextField
import com.apicta.myoscopealert.ui.SimpleOutlinedTextFieldSample
import com.apicta.myoscopealert.ui.Visibility
import com.apicta.myoscopealert.ui.VisibilityOff

@Composable
fun LoginPage(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                color = Color.Transparent,
            )
    ) {


        Box(
            modifier = Modifier
                /*.background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(25.dp, 5.dp, 25.dp, 5.dp)
                )*/
                .align(Alignment.BottomCenter),
        ) {

            Image(
                painter = painterResource(id = R.drawable.user_sign_in),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth(),

                )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                ,

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //.........................Spacer
                Spacer(modifier = Modifier.height(50.dp))

                //.........................Text: title
                androidx.compose.material3.Text(
                    text = "Sign In",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 130.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
//                SimpleOutlinedTextFieldSample("")

                Spacer(modifier = Modifier.padding(3.dp))
//                SimpleOutlinedPasswordTextField()

                val gradientColor = listOf(Color(0xFF484BF1), Color(0xFF673AB7))
                val cornerRadius = 16.dp


                Spacer(modifier = Modifier.padding(10.dp))
                /* Button(
                     onClick = {},
                     modifier = Modifier
                         .fillMaxWidth(0.8f)
                         .height(50.dp)
                 ) {
                     Text(text = "Login", fontSize = 20.sp)
                 }*/
//                GradientButton(
//                    gradientColors = gradientColor,
//                    cornerRadius = cornerRadius,
//                    nameButton = "Login",
//                    roundedCornerShape = RoundedCornerShape(topStart = 30.dp,bottomEnd = 30.dp)
//                )

//                Spacer(modifier = Modifier.padding(10.dp))
//                androidx.compose.material3.TextButton(onClick = {
//
//                    navController.navigate("register_page"){
//                        popUpTo(navController.graph.startDestinationId)
//                        launchSingleTop = true
//                    }
//
//                }) {
//                    androidx.compose.material3.Text(
//                        text = "Create An Account",
//                        letterSpacing = 1.sp,
//                        style = MaterialTheme.typography.labelLarge
//                    )
//                }
//
//
//                Spacer(modifier = Modifier.padding(5.dp))
//                androidx.compose.material3.TextButton(onClick = {
//
//                    navController.navigate("reset_page"){
//                        popUpTo(navController.graph.startDestinationId)
//                        launchSingleTop = true
//                    }
//
//                }) {
//                    androidx.compose.material3.Text(
//                        text = "Reset Password",
//                        letterSpacing = 1.sp,
//                        style = MaterialTheme.typography.labelLarge,
//                    )
//                }
//                Spacer(modifier = Modifier.padding(20.dp))

            }


        }

    }


}


//...........................................................................




@Preview(showBackground = true)
@Composable
fun LoginTestPrev() {
    LoginPage(navController = rememberNavController())
}