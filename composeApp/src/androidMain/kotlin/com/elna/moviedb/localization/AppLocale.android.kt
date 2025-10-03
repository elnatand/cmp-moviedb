package com.elna.moviedb.localization

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale


actual object LocalAppLocale {
    private var default: Locale? = null

    actual val current: String
        @Composable get() = Locale.getDefault().toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current

        if (default == null) {
            default = Locale.getDefault()
        }

        val new = when (value) {
            null -> default ?: Locale.getDefault()
            else -> Locale.forLanguageTag(value)
        }

        Locale.setDefault(new)

        val newConfig = Configuration(configuration).apply {
            setLocale(new)
        }

        return LocalConfiguration.provides(newConfig)
    }
}
