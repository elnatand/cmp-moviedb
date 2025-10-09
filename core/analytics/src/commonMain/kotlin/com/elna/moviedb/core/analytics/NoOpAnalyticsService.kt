package com.elna.moviedb.core.analytics

/**
 * No-op implementation of AnalyticsService
 * Used when analytics is not configured or disabled.
 * All methods are empty implementations that do nothing.
 */
class NoOpAnalyticsService : AnalyticsService {
    override val isEnabled: Boolean = false

    override fun logScreenView(screenName: String, screenClass: String?) {
        // No-op: Analytics not configured
    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        // No-op: Analytics not configured
    }

    override fun setUserProperty(name: String, value: String) {
        // No-op: Analytics not configured
    }

    override fun setUserId(userId: String?) {
        // No-op: Analytics not configured
    }
}
