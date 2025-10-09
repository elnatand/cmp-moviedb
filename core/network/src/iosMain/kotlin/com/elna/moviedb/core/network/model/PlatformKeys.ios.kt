package com.elna.moviedb.core.network.model

import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.dictionaryWithContentsOfFile


actual val TMDB_API_KEY: String = NSBundle.mainBundle.pathForResource("Secrets", "plist")?.let {
    val map = NSDictionary.dictionaryWithContentsOfFile(it)
    map?.get("tmdbApiKey") as? String
} ?: error("TMDB_API_KEY not found in Secrets.plist")