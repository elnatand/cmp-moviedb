package com.elna.moviedb.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.localization.LocalAppLocale

import org.koin.compose.koinInject


@Composable
fun Localization(content: @Composable () -> Unit) {
    val preferencesManager: PreferencesManager = koinInject()
    val selectedLanguage by preferencesManager.getAppLanguageCode()
        .collectAsStateWithLifecycle(initialValue = "en")

    // Update custom locale when language changes
    LaunchedEffect(selectedLanguage) {
      //  customAppLocale = selectedLanguage
    }

    // Determine layout direction based on language
    val layoutDirection = when (selectedLanguage) {
        "ar", "he" -> LayoutDirection.Rtl // Arabic and Hebrew use RTL
        else -> LayoutDirection.Ltr // Default LTR for English and Hindi
    }

    CompositionLocalProvider(
        LocalAppLocale provides customAppLocale,
        LocalLayoutDirection provides layoutDirection
    ) {
        // Key causes recomposition when language changes
        key(customAppLocale) {
            content()
        }
    }
}