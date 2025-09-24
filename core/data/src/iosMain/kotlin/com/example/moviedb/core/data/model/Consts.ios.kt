package com.example.moviedb.core.data.model

import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.dictionaryWithContentsOfFile


actual val API_KEY: String = NSBundle.mainBundle.pathForResource("Secrets", "plist")?.let {
    val map = NSDictionary.dictionaryWithContentsOfFile(it)
    map?.get("apiKey") as? String
} ?: ""