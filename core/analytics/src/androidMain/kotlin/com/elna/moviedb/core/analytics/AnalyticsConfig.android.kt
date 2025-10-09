package com.elna.moviedb.core.analytics

import android.util.Log


/**
 * Check if analytics is enabled on Android
 * Analytics is enabled if GOOGLE_ANALYTICS_ID is not empty
 */
actual val analyticsEnabled: Boolean
    get() =

/**
 * Create Android-specific analytics service
 *
 * Returns NoOpAnalyticsService if:
 * - No analytics ID is configured
 * - Firebase fails to initialize
 */
actual fun createAnalyticsService(): AnalyticsService {
    return if (analyticsEnabled) {
        try {
            val service = FirebaseAnalyticsService()
            // Check if Firebase actually initialized
            if (service.isEnabled) {
                Log.d("Analytics", "Firebase Analytics initialized successfully")
                service
            } else {
                Log.w("Analytics", "Firebase Analytics not available, using NoOp")
                NoOpAnalyticsService()
            }
        } catch (e: Exception) {
            Log.e("Analytics", "Failed to create FirebaseAnalyticsService: ${e.message}")
            NoOpAnalyticsService()
        }
    } else {
        Log.d("Analytics", "Analytics disabled - no GOOGLE_ANALYTICS_ID configured")
        NoOpAnalyticsService()
    }
}
