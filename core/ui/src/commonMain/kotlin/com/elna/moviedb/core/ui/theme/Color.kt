package com.elna.moviedb.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── CineDB Noir Dark Palette ──────────────────────────────────────────────────
private val DarkPrimary = Color(0xFFB4C5FF)           // Periwinkle Blue — accent, links, outlines
private val DarkOnPrimary = Color(0xFF002A77)
private val DarkPrimaryContainer = Color(0xFF195DE6)  // Deep Action Blue — CTA buttons
private val DarkOnPrimaryContainer = Color(0xFFE2E7FF)
private val DarkSecondary = Color(0xFFC7C5D3)         // Lavender Mist — secondary text
private val DarkOnSecondary = Color(0xFF302F3A)
private val DarkSecondaryContainer = Color(0xFF494853) // Dusk Purple — inactive chips
private val DarkOnSecondaryContainer = Color(0xFFB9B7C4)
private val DarkTertiary = Color(0xFFE9C400)          // Cinema Gold — star ratings only
private val DarkOnTertiary = Color(0xFF3A3000)
private val DarkTertiaryContainer = Color(0xFFC9A900)
private val DarkOnTertiaryContainer = Color(0xFF4C3F00)
private val DarkError = Color(0xFFFFB4AB)             // Coral Warning
private val DarkOnError = Color(0xFF690005)
private val DarkErrorContainer = Color(0xFF93000A)
private val DarkOnErrorContainer = Color(0xFFFFDAD6)
private val DarkBackground = Color(0xFF131318)        // Cinema Black — page base
private val DarkOnBackground = Color(0xFFE4E1E9)      // Moonlight White — primary text
private val DarkSurface = Color(0xFF1F1F25)           // Charcoal Card — standard containers
private val DarkOnSurface = Color(0xFFE4E1E9)
private val DarkSurfaceVariant = Color(0xFF35343A)
private val DarkOnSurfaceVariant = Color(0xFFC3C6D7)  // Silver Mist — secondary text
private val DarkOutline = Color(0xFF8D90A0)           // Slate Outline — borders, inactive icons
private val DarkOutlineVariant = Color(0xFF434655)    // Shadow Outline — subtle separators

// ── Light Palette (kept for theme-switcher support) ──────────────────────────
private val LightPrimary = Color(0xFF0053DA)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFDBE1FF)
private val LightOnPrimaryContainer = Color(0xFF001849)
private val LightSecondary = Color(0xFF5B5A6F)
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightSecondaryContainer = Color(0xFFE0DEF4)
private val LightOnSecondaryContainer = Color(0xFF18172B)
private val LightTertiary = Color(0xFF6F5E00)
private val LightOnTertiary = Color(0xFFFFFFFF)
private val LightTertiaryContainer = Color(0xFFFFE16D)
private val LightOnTertiaryContainer = Color(0xFF221B00)
private val LightError = Color(0xFFBA1A1A)
private val LightOnError = Color(0xFFFFFFFF)
private val LightErrorContainer = Color(0xFFFFDAD6)
private val LightOnErrorContainer = Color(0xFF410002)
private val LightBackground = Color(0xFFFBF8FF)
private val LightOnBackground = Color(0xFF1B1B21)
private val LightSurface = Color(0xFFFBF8FF)
private val LightOnSurface = Color(0xFF1B1B21)
private val LightSurfaceVariant = Color(0xFFE3E1EC)
private val LightOnSurfaceVariant = Color(0xFF46454F)
private val LightOutline = Color(0xFF777680)
private val LightOutlineVariant = Color(0xFFC7C5D0)

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)
