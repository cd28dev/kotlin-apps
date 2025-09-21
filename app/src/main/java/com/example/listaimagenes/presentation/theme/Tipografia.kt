package com.example.listaimagenes.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    titleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle( // 🆕 AGREGAR ESTE ESTILO
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    )
)