package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AviationSky,
    onPrimary = Slate900,
    primaryContainer = AviationNavy,
    onPrimaryContainer = AviationSkyLight,
    secondary = GoldLegal,
    onSecondary = Slate900,
    secondaryContainer = Slate800,
    onSecondaryContainer = GoldLegalLight,
    background = AviationNavyDark,
    surface = Slate900,
    onBackground = Slate100,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300
)

private val LightColorScheme = lightColorScheme(
    primary = AviationNavy,
    onPrimary = PureWhite,
    primaryContainer = AviationSkyLight,
    onPrimaryContainer = AviationNavy,
    secondary = GoldLegal,
    onSecondary = PureWhite,
    secondaryContainer = GoldLegalLight,
    onSecondaryContainer = Slate900,
    background = Slate50,
    surface = PureWhite,
    onBackground = Slate900,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700
)

@Composable
fun FlightClaimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded aviation identity
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
