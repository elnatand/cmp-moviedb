package com.elna.moviedb.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue


/**
 * Platform-specific locale provider
 * Implementations must provide the current locale and a way to override it
 */
expect object LocalAppLocale {

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}


