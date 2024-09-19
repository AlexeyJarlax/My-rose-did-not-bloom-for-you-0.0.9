package com.pavlovalexey.pleinair.main.ui.uiComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavlovalexey.pleinair.R

@Composable
fun CustomButtonOne(
    onClick: () -> Unit,
    text: String,
    iconResId: Int,
    modifier: Modifier = Modifier,
    textColor: Color = colorResource(id = R.color.my_prime_day),
    iconColor: Color = colorResource(id = R.color.my_prime_day)
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = textColor // Используем textColor для contentColor
        ),
        modifier = modifier
            .padding(end = 12.dp, bottom = 12.dp)
            .background(Color.Transparent)
            .height(IntrinsicSize.Min),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(0.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 22.sp,
            letterSpacing = 0.0.sp,
            fontFamily = FontFamily.Default,
            color = textColor
        )
    }
}