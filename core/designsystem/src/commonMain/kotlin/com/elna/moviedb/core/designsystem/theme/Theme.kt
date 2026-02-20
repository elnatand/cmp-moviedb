package com.elna.moviedb.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.elna.moviedb.core.model.ThemeConfig

@Composable
fun AppTheme(
    themeConfig: String,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeConfig) {
        ThemeConfig.LIGHT.value -> false
        ThemeConfig.DARK.value -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDarkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
