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
    ROYAL_GOLD(
        id = "ROYAL_GOLD",
        displayName = "Royal Sapphire & Gold",
        subtitle = "Signature luxury navy with royal gold accents",
        previewPrimary = Color(0xFF0F172A),
        previewAccent = Color(0xFFD4AF37)
    ),
    EMERALD_LUXURY(
        id = "EMERALD_LUXURY",
        displayName = "Emerald Grandeur",
        subtitle = "Imperial deep forest green with vibrant emerald & sage",
        previewPrimary = Color(0xFF064E3B),
        previewAccent = Color(0xFF10B981)
    ),
    CRIMSON_REGAL(
        id = "CRIMSON_REGAL",
        displayName = "Crimson Regal",
        subtitle = "Palatial deep burgundy with ruby rose & champagne",
        previewPrimary = Color(0xFF4C0519),
        previewAccent = Color(0xFFFB7185)
    ),
    OCEAN_AZURE(
        id = "OCEAN_AZURE",
        displayName = "Ocean Azure",
        subtitle = "Prestige nautical deep blue with radiant cyan highlights",
        previewPrimary = Color(0xFF0C4A6E),
        previewAccent = Color(0xFF38BDF8)
    ),
    MIDNIGHT_OBSIDIAN(
        id = "MIDNIGHT_OBSIDIAN",
        displayName = "Midnight Obsidian",
        subtitle = "Modern stealth obsidian graphite with neon violet accents",
        previewPrimary = Color(0xFF18181B),
        previewAccent = Color(0xFFA855F7)
    );

    companion object {
        fun fromId(id: String): AppTheme = values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: ROYAL_GOLD
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

val RoyalGoldPalette = AppThemePalette(
    primary = Color(0xFF0F172A),
    secondary = Color(0xFF1E293B),
    primaryLight = Color(0xFF334155),
    accent = Color(0xFFD4AF37),
    accentSecondary = Color(0xFFC5A059),
    accentContainer = Color(0xFFFEF3C7),
    onAccentContainer = Color(0xFF78350F)
)

val EmeraldLuxuryPalette = AppThemePalette(
    primary = Color(0xFF064E3B),
    secondary = Color(0xFF065F46),
    primaryLight = Color(0xFF047857),
    accent = Color(0xFF10B981),
    accentSecondary = Color(0xFF34D399),
    accentContainer = Color(0xFFD1FAE5),
    onAccentContainer = Color(0xFF064E3B)
)

val CrimsonRegalPalette = AppThemePalette(
    primary = Color(0xFF4C0519),
    secondary = Color(0xFF881337),
    primaryLight = Color(0xFF9F1239),
    accent = Color(0xFFFB7185),
    accentSecondary = Color(0xFFF43F5E),
    accentContainer = Color(0xFFFFE4E6),
    onAccentContainer = Color(0xFF881337)
)

val OceanAzurePalette = AppThemePalette(
    primary = Color(0xFF0C4A6E),
    secondary = Color(0xFF0369A1),
    primaryLight = Color(0xFF0284C7),
    accent = Color(0xFF38BDF8),
    accentSecondary = Color(0xFF0EA5E9),
    accentContainer = Color(0xFFE0F2FE),
    onAccentContainer = Color(0xFF0369A1)
)

val MidnightObsidianPalette = AppThemePalette(
    primary = Color(0xFF18181B),
    secondary = Color(0xFF27272A),
    primaryLight = Color(0xFF3F3F46),
    accent = Color(0xFFA855F7),
    accentSecondary = Color(0xFFC084FC),
    accentContainer = Color(0xFFF3E8FF),
    onAccentContainer = Color(0xFF581C87)
)

fun getPaletteForTheme(theme: AppTheme): AppThemePalette {
    return when (theme) {
        AppTheme.ROYAL_GOLD -> RoyalGoldPalette
        AppTheme.EMERALD_LUXURY -> EmeraldLuxuryPalette
        AppTheme.CRIMSON_REGAL -> CrimsonRegalPalette
        AppTheme.OCEAN_AZURE -> OceanAzurePalette
        AppTheme.MIDNIGHT_OBSIDIAN -> MidnightObsidianPalette
    }
}

val LocalAppThemePalette = staticCompositionLocalOf { RoyalGoldPalette }
