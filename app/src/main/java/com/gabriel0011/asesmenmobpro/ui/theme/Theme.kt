package com.gabriel0011.asesmenmobpro.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OrangeGym,
    onPrimary = Color.White,
    primaryContainer = OrangeGymDark,
    background = DarkGrey,
    surface = SurfaceGrey,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeGym,
    onPrimary = Color.White,
    primaryContainer = OrangeGymLight,
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    onPrimaryContainer = Color(0xFF3E2723)
)

@Composable
fun Mobpro1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: Int = 0,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val baseColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val colorScheme = when (themeColor) {
        1 -> baseColorScheme.copy(primary = androidx.compose.ui.graphics.Color(0xFFD32F2F))
        2 -> baseColorScheme.copy(primary = androidx.compose.ui.graphics.Color(0xFF388E3C))
        else -> baseColorScheme.copy(primary = androidx.compose.ui.graphics.Color(0xFF1976D2))
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}