package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppTheme(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val previewPrimary: Color,
    val previewAccent: Color
) {
    ARIHANT_CLASSIC(
        id = "ARIHANT_CLASSIC",
        displayName = "Arihant Classic",
        subtitle = "Signature residential deep navy with rich gold accents",
        previewPrimary = Color(0xFF0F2850),
        previewAccent = Color(0xFFD4AF37)
    ),
    MODERN_BLUE(
        id = "MODERN_BLUE",
        displayName = "Modern Blue",
        subtitle = "Crisp, contemporary cobalt blue with sky azure highlights",
        previewPrimary = Color(0xFF1E40AF),
        previewAccent = Color(0xFF38BDF8)
    ),
    GREEN_COMMUNITY(
        id = "GREEN_COMMUNITY",
        displayName = "Green Community",
        subtitle = "Eco-friendly botanical forest green with crisp mint",
        previewPrimary = Color(0xFF065F46),
        previewAccent = Color(0xFF10B981)
    ),
    PROFESSIONAL_INDIGO(
        id = "PROFESSIONAL_INDIGO",
        displayName = "Professional Indigo",
        subtitle = "Executive deep indigo with luminous violet tones",
        previewPrimary = Color(0xFF4338CA),
        previewAccent = Color(0xFF818CF8)
    ),
    ROYAL_GOLD(
        id = "ROYAL_GOLD",
        displayName = "Royal Sapphire & Gold",
        subtitle = "Signature luxury navy with royal gold accents",
        previewPrimary = Color(0xFF0F172A),
        previewAccent = Color(0xFFD4AF37)
    );

    companion object {
        fun fromId(id: String): AppTheme = values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: ARIHANT_CLASSIC
    }
}

data class AppThemePalette(
    val primary: Color,
    val secondary: Color,
    val primaryLight: Color,
    val accent: Color,
    val accentSecondary: Color,
    val accentContainer: Color,
    val onAccentContainer: Color,
    val background: Color = OffWhiteBackground,
    val surface: Color = PureWhiteSurface,
    val surfaceVariant: Color = SurfaceVariant,
    val border: Color = BorderSubtle,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textMuted: Color = TextMuted
) {
    fun toMaterialColorScheme(): ColorScheme {
        return lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = secondary,
            onPrimaryContainer = accentContainer,
            secondary = accentSecondary,
            onSecondary = Color.White,
            secondaryContainer = accentContainer,
            onSecondaryContainer = onAccentContainer,
            tertiary = accent,
            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = textSecondary,
            outline = border,
            error = StatusCritical,
            onError = Color.White
        )
    }
}

val ArihantClassicPalette = AppThemePalette(
    primary = Color(0xFF0F2850),
    secondary = Color(0xFF1E3A6C),
    primaryLight = Color(0xFF2C5282),
    accent = Color(0xFFD4AF37),
    accentSecondary = Color(0xFFC5A059),
    accentContainer = Color(0xFFFEF3C7),
    onAccentContainer = Color(0xFF78350F)
)

val ModernBluePalette = AppThemePalette(
    primary = Color(0xFF1E40AF),
    secondary = Color(0xFF2563EB),
    primaryLight = Color(0xFF3B82F6),
    accent = Color(0xFF38BDF8),
    accentSecondary = Color(0xFF0EA5E9),
    accentContainer = Color(0xFFE0F2FE),
    onAccentContainer = Color(0xFF0369A1)
)

val GreenCommunityPalette = AppThemePalette(
    primary = Color(0xFF065F46),
    secondary = Color(0xFF047857),
    primaryLight = Color(0xFF10B981),
    accent = Color(0xFF10B981),
    accentSecondary = Color(0xFF34D399),
    accentContainer = Color(0xFFD1FAE5),
    onAccentContainer = Color(0xFF064E3B)
)

val ProfessionalIndigoPalette = AppThemePalette(
    primary = Color(0xFF4338CA),
    secondary = Color(0xFF4F46E5),
    primaryLight = Color(0xFF6366F1),
    accent = Color(0xFF818CF8),
    accentSecondary = Color(0xFFA5B4FC),
    accentContainer = Color(0xFFEEF2FF),
    onAccentContainer = Color(0xFF3730A3)
)

val RoyalGoldPalette = ArihantClassicPalette

fun getPaletteForTheme(theme: AppTheme): AppThemePalette {
    return when (theme) {
        AppTheme.ARIHANT_CLASSIC -> ArihantClassicPalette
        AppTheme.MODERN_BLUE -> ModernBluePalette
        AppTheme.GREEN_COMMUNITY -> GreenCommunityPalette
        AppTheme.PROFESSIONAL_INDIGO -> ProfessionalIndigoPalette
        AppTheme.ROYAL_GOLD -> ArihantClassicPalette
    }
}

val LocalAppThemePalette = staticCompositionLocalOf { ArihantClassicPalette }
