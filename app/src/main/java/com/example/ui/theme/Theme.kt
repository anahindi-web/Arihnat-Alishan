package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = NavyPrimary,
    primaryContainer = NavySecondary,
    onPrimaryContainer = GoldContainer,
    secondary = GoldChampagne,
    onSecondary = NavyPrimary,
    background = DarkNavyBg,
    onBackground = Color.White,
    surface = DarkNavySurface,
    onSurface = Color.White,
    surfaceVariant = DarkNavyCard,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = DarkBorder,
    error = StatusCritical,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = NavySecondary,
    onPrimaryContainer = GoldContainer,
    secondary = GoldChampagne,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = OnGoldContainer,
    tertiary = GoldAccent,
    background = OffWhiteBackground,
    onBackground = TextPrimary,
    surface = PureWhiteSurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = StatusCritical,
    onError = Color.White
)

@Composable
fun ArihantAlishanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand colors for luxury aesthetic
    appTheme: AppTheme = AppTheme.ROYAL_GOLD,
    content: @Composable () -> Unit
) {
    val palette = getPaletteForTheme(appTheme)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> palette.toMaterialColorScheme()
    }

    CompositionLocalProvider(LocalAppThemePalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Alias for backwards compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = ArihantAlishanTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)

