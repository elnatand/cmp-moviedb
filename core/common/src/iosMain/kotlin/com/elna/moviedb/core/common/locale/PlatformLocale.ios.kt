package com.elna.moviedb.core.common.locale

import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UIKit.UIApplication
import platform.UIKit.UIUserInterfaceLayoutDirection

actual val platformLanguage: String
    get() = NSLocale.currentLocale.languageCode

actual val platformCountry: String
    get() = NSLocale.currentLocale.countryCode?: ""