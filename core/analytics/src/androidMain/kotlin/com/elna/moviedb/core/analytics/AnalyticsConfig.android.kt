package com.elna.moviedb.core.analytics

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics


/**
 * Check if analytics is enabled on Android
 */
actual val analyticsEnabled: Boolean
    get() = try {
        // Accessing Firebase.analytics will throw if Firebase isn't set up (e.g., missing google-services)
        Firebase.analytics
        true
    } catch (e: Exception) {
        Log.d("Analytics", "Firebase Analytics not available: ${e.message}")
        false
    }

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
