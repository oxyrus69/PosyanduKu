package com.posyanduku.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun posyanduTypography(scale: Float): Typography {
    fun s(base: Float) = (base * scale).sp
    val sans = FontFamily.Default
    return Typography(
        displaySmall = TextStyle(fontFamily = sans, fontWeight = FontWeight.Bold, fontSize = s(28f), lineHeight = s(34f)),
        headlineLarge = TextStyle(fontFamily = sans, fontWeight = FontWeight.Bold, fontSize = s(24f), lineHeight = s(30f)),
        headlineMedium = TextStyle(fontFamily = sans, fontWeight = FontWeight.Bold, fontSize = s(21f), lineHeight = s(27f)),
        titleLarge = TextStyle(fontFamily = sans, fontWeight = FontWeight.Bold, fontSize = s(19f), lineHeight = s(25f)),
        titleMedium = TextStyle(fontFamily = sans, fontWeight = FontWeight.SemiBold, fontSize = s(17f), lineHeight = s(23f)),
        titleSmall = TextStyle(fontFamily = sans, fontWeight = FontWeight.SemiBold, fontSize = s(15.5f), lineHeight = s(21f)),
        bodyLarge = TextStyle(fontFamily = sans, fontWeight = FontWeight.Normal, fontSize = s(17f), lineHeight = s(25f)),
        bodyMedium = TextStyle(fontFamily = sans, fontWeight = FontWeight.Normal, fontSize = s(15.5f), lineHeight = s(22f)),
        bodySmall = TextStyle(fontFamily = sans, fontWeight = FontWeight.Normal, fontSize = s(13.5f), lineHeight = s(19f)),
        labelLarge = TextStyle(fontFamily = sans, fontWeight = FontWeight.SemiBold, fontSize = s(16f), lineHeight = s(21f)),
        labelMedium = TextStyle(fontFamily = sans, fontWeight = FontWeight.Medium, fontSize = s(14f), lineHeight = s(18f)),
        labelSmall = TextStyle(fontFamily = sans, fontWeight = FontWeight.Medium, fontSize = s(12.5f), lineHeight = s(16f)),
    )
}
