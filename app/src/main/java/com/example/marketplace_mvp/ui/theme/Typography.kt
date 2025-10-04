package com.example.marketplace_mvp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.marketplace_mvp.R


val AppFont = FontFamily(
    Font(R.font.wixmadefortext_regular, weight = FontWeight.Normal),
    Font(R.font.wixmadefortext_medium, weight = FontWeight.Medium),
    Font(R.font.wixmadefortext_semibold, weight = FontWeight.SemiBold),
    Font(R.font.wixmadefortext_bold, weight = FontWeight.Bold),
    Font(R.font.wixmadefortext_extrabold, weight = FontWeight.ExtraBold),
)
// Custom font (if you have it in res/font)

val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = AppFont,
        fontSize = 18.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AppFont,
        fontSize = 24.sp
    ),
    bodySmall = TextStyle(
        fontFamily = AppFont,
        fontSize = 14.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFont,
        fontSize = 18.sp
    ),
)
