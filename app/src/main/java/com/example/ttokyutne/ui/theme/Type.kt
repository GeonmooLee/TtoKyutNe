package com.example.ttokyutne.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun rememberAppFontFamily(): FontFamily {
    val assetManager = LocalContext.current.assets
    return remember(assetManager) {
        FontFamily(
            Font("fonts/nanum_square_round_r.ttf", assetManager, FontWeight.Normal),
            Font("fonts/nanum_square_round_r.ttf", assetManager, FontWeight.Medium),
            Font("fonts/nanum_square_round_b.ttf", assetManager, FontWeight.SemiBold),
            Font("fonts/nanum_square_round_b.ttf", assetManager, FontWeight.Bold),
            Font("fonts/nanum_square_round_eb.ttf", assetManager, FontWeight.ExtraBold),
            Font("fonts/nanum_square_round_eb.ttf", assetManager, FontWeight.Black)
        )
    }
}

@Composable
fun rememberAppTypography(): Typography {
    val fontFamily = rememberAppFontFamily()
    return remember(fontFamily) {
        Typography(
            bodyLarge = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp
            )
        )
    }
}

val PreviewFallbackTypography: Typography
    get() = Typography(
        bodyLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        )
    )
