package com.elna.moviedb.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.elna.moviedb.localization.LocalAppLocale


@Composable
fun Localization(selectedLanguage: String, content: @Composable () -> Unit) {

    // Determine layout direction based on language
    val layoutDirection = when (selectedLanguage) {
        "ar", "he" -> LayoutDirection.Rtl // Arabic and Hebrew use RTL
        else -> LayoutDirection.Ltr // Default LTR for English and Hindi
    }

    CompositionLocalProvider(
        LocalAppLocale provides selectedLanguage,
        LocalLayoutDirection provides layoutDirection
    ) {
        // Key causes recomposition when language changes
        key(selectedLanguage) {
            content()
        }
    }
}