package com.elna.moviedb.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.elna.moviedb.localization.LocalAppLocale

@Composable
actual fun Localization(selectedLanguage: String, content: @Composable () -> Unit) {
    val layoutDirection = when (selectedLanguage) {
        "ar", "he" -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }

    CompositionLocalProvider(
        LocalAppLocale provides selectedLanguage,
        LocalLayoutDirection provides layoutDirection
    ) {
        key(selectedLanguage) {
            content()
        }
    }
}
