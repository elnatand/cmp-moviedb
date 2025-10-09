package com.elna.moviedb.core.analytics

import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.dictionaryWithContentsOfFile

/**
 * Get Google Analytics ID from Secrets.plist (optional)
 * Returns null if not configured
 */
private val googleAnalyticsId: String? = NSBundle.mainBundle.pathForResource("Secrets", "plist")?.let {
    val map = NSDictionary.dictionaryWithContentsOfFile(it)
    map?.get("googleAnalyticsId") as? String
}

/**
 * Check if analytics is enabled on iOS
 * Analytics is enabled if googleAnalyticsId is present in Secrets.plist
 */
actual val analyticsEnabled: Boolean
    get() = !googleAnalyticsId.isNullOrEmpty()

/**
 * Create iOS-specific analytics service
 */
actual fun createAnalyticsService(): AnalyticsService {
    return if (analyticsEnabled) {
        FirebaseAnalyticsServiceIOS()
    } else {
        NoOpAnalyticsService()
    }
}
