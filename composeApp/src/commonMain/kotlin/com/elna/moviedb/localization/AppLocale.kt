package com.elna.moviedb.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


/**
 * Platform-specific locale provider
 * Implementations must provide the current locale and a way to override it
 */
expect object LocalAppLocale {
    val current: String

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}


