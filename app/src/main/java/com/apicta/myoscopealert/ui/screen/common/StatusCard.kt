package com.apicta.myoscopealert.ui.screen.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apicta.myoscopealert.ui.theme.terniary

@Composable
fun RowScope.StatusCard(
    number: Int,
    icon: ImageVector,
    title: String,
    accent: Color,
    second: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .weight(1f)
            .background(color = second, shape = RoundedCornerShape(16.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = icon,
            modifier = modifier.size(38.dp),
            tint = accent,
            contentDescription = ""
        )

        Column(modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)) {
            Text(text = number.toString(), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun RowScope.StatusCardShimmer(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .weight(1f)
            .background(color = Color(0x83E7E7E7), shape = RoundedCornerShape(16.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
            },
            modifier = modifier
                .size(36.dp)
                .align(Alignment.CenterVertically)
                .clip(shape = CircleShape)
                .shimmerEffect()
        ) {
        }

        Column(modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)) {
            Text(text = "", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, modifier = modifier
                .width(32.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .shimmerEffect())
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = modifier
                .width(70.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .shimmerEffect())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusCardPrev() {
}