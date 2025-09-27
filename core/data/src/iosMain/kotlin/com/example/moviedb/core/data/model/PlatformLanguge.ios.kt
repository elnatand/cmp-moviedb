package com.example.moviedb.core.data.model

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UIKit.UIApplication
import platform.UIKit.UIUserInterfaceLayoutDirection

actual val platformLanguage: String
    get() = NSLocale.currentLocale.languageCode
