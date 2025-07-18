package com.faysal.zenify.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.faysal.zenify.R


val NavFont = FontFamily(
    Font(R.font.nav_font, FontWeight.Bold),
)


val Nunito = FontFamily(
    Font(R.font.nunito_nans_regular, FontWeight.Normal),
    Font(R.font.nunito_sans_bold, FontWeight.Bold),
    Font(R.font.nunito_sans_light, FontWeight.Light),
    Font(R.font.nunito_sans_medium, FontWeight.Medium),
    Font(R.font.nunito_sans_italic, FontWeight.Thin)
)

val AvenirNext = FontFamily(
    Font(R.font.avenir_next_cyr_regular, FontWeight.Normal),
    Font(R.font.avenir_next_cyr_bold, FontWeight.Bold),
    Font(R.font.avenir_next_cyr_light, FontWeight.Light),
    Font(R.font.avenir_next_cyr_medium, FontWeight.Medium)
)


val ProductSans = FontFamily(
    Font(R.font.product_sans_regular, FontWeight.Normal),
    Font(R.font.product_sans_bold, FontWeight.Bold),
    Font(R.font.product_sans_light, FontWeight.Light),
    Font(R.font.product_sans_medium, FontWeight.Medium),
    Font(R.font.product_sans_thin, FontWeight.Thin),
    Font(R.font.product_sans_black, FontWeight.Black),
)

val OpenSans = FontFamily(
    Font(R.font.open_sans_regular, FontWeight.Normal),
    Font(R.font.open_sans_bold, FontWeight.Bold),
    Font(R.font.open_sans_light, FontWeight.Light)
)

val Barlow = FontFamily(
    Font(R.font.barlow_regular, FontWeight.Normal),
    Font(R.font.barlow_bold, FontWeight.Bold),
    Font(R.font.barlow_extra_bold, FontWeight.ExtraBold),
)


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = ProductSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = ProductSans,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ProductSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)