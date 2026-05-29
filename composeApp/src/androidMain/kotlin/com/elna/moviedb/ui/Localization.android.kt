@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.elna.moviedb.ui

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import org.jetbrains.compose.resources.ComposeEnvironment
import org.jetbrains.compose.resources.DensityQualifier
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.LanguageQualifier
import org.jetbrains.compose.resources.LocalComposeEnvironment
import org.jetbrains.compose.resources.RegionQualifier
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.ThemeQualifier
import java.util.Locale

// Overrides CMP's internal LocalComposeEnvironment to directly control which locale
// stringResource() uses. Necessary because Locale.setDefault() can be overridden by
// OEM skins (OnePlus/OxygenOS) and resources.updateConfiguration() is deprecated.
// Uses selectedLanguage (BCP 47) directly — NOT Locale.getLanguage() which returns
// legacy codes ("iw" for Hebrew, "in" for Indonesian) that don't match CMP qualifiers.
@OptIn(InternalResourceApi::class)
private class LocaleComposeEnvironment(private val language: String, private val region: String) :
    ComposeEnvironment {

    @Composable
    override fun rememberEnvironment(): ResourceEnvironment {
        val isDark = isSystemInDarkTheme()
        val density = LocalDensity.current.density
        return ResourceEnvironment(
            LanguageQualifier(language),
            RegionQualifier(region),
            ThemeQualifier.selectByValue(isDark),
            DensityQualifier.selectByDensity(density)
        )
    }
}

@OptIn(InternalResourceApi::class)
@Composable
actual fun Localization(selectedLanguage: String, content: @Composable () -> Unit) {
    val locale = Locale.forLanguageTag(selectedLanguage)
    Locale.setDefault(locale)

    val newConfig = Configuration(LocalConfiguration.current).apply {
        setLocale(locale)
    }
    val localizedContext = LocalContext.current.createConfigurationContext(newConfig)

    val layoutDirection = when (selectedLanguage) {
        "ar", "he" -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }

    CompositionLocalProvider(
        LocalComposeEnvironment provides LocaleComposeEnvironment(selectedLanguage, ""),
        LocalConfiguration provides newConfig,
        LocalContext provides localizedContext,
        LocalLayoutDirection provides layoutDirection
    ) {
        key(selectedLanguage) {
            content()
        }
    }
}
