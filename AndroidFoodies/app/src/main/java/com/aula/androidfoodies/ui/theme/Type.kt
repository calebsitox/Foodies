@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
package com.aula.androidfoodies.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

import com.aula.androidfoodies.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = emptyList()
//R.array.com_google_android_gms_fonts_certs // Use an empty list if you don't have certs
)
val fontFoodiess = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.font_foodies, FontWeight.W400),

)
// Set up Google Font
val playwriteFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Lobster"),
        fontProvider = provider,
    )
)

val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = playwriteFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = playwriteFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    )
)
