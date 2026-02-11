package com.example.ashrut.findx.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF075E54),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF005C4B),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF25D366),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCF8C6),
    onSecondaryContainer = Color(0xFF075E54),
    background = Color(0xFF0B141A),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C252C),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2A3942),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00A884),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCF8C6),
    onPrimaryContainer = Color(0xFF075E54),
    secondary = Color(0xFF25D366),
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = Color(0xFF056162),
    onSecondaryContainer = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFF7F8FA),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFCF6679),
    onError = Color(0xFF1C1B1F)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun FindXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}