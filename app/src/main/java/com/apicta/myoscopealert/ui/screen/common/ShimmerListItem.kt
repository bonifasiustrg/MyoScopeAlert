package com.apicta.myoscopealert.ui.screen.common

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apicta.myoscopealert.R
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
import com.apicta.myoscopealert.ui.theme.terniary
import java.util.Date

@Composable
fun ShimmerListItem(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    if(isLoading) {
        Row(
            modifier
                .fillMaxWidth()
                .background(color = Color(0x83E7E7E7), shape = RoundedCornerShape(16.dp))
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Column(modifier.weight(2f)) {
                Text(
                    text = "",
                    fontWeight = FontWeight.Bold,
                    modifier = modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 8.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .shimmerEffect()
                        .clip(
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .height(32.dp)
                        .fillMaxWidth(0.6f)
                        .padding(bottom = 16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .shimmerEffect()
                ) {
                }
            }
            Spacer(modifier = modifier.height(12.dp))

            IconButton(
                onClick = {
                },
                modifier = modifier
                    .size(44.dp)
                    .align(Alignment.CenterVertically)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .shimmerEffect()
                    .clip(shape = CircleShape)
            ) {
            }
            Spacer(modifier = modifier.height(8.dp))
        }
        Spacer(modifier = modifier.height(8.dp))


    } else {
        contentAfterLoading()
    }
}

@Composable
fun ShimmerCardStatistic(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    if(isLoading) {

        Column {
            Row(modifier.fillMaxWidth()) {
                StatusCardShimmer()
                Spacer(modifier = modifier.width(12.dp))
                StatusCardShimmer()
            }
            Spacer(modifier = modifier.height(12.dp))
            Row(modifier.fillMaxWidth()) {
                StatusCardShimmer()

                Spacer(modifier = modifier.width(12.dp))
                StatusCardShimmer()

            }
        }

    } else {
        contentAfterLoading()
    }
}


fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        )
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}