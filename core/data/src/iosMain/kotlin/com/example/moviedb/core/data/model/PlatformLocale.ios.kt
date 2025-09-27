package com.example.moviedb.core.data.model

import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual val platformLanguage: String
    get() = NSLocale.currentLocale.languageCode

actual val platformCountry: String
    get() = NSLocale.currentLocale.countryCode?: ""