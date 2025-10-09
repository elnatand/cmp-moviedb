package com.elna.moviedb.core.analytics

/**
 * Analytics service interface for tracking app events and user interactions.
 *
 * This service is designed to work even when analytics is not configured.
 * All implementations should handle missing configuration gracefully.
 */
interface AnalyticsService {

    /**
     * Check if analytics is enabled and properly configured
     */
    val isEnabled: Boolean

    /**
     * Log a screen view event
     * @param screenName The name of the screen being viewed
     * @param screenClass The class name of the screen (optional)
     */
    fun logScreenView(screenName: String, screenClass: String? = null)

    /**
     * Log a custom event
     * @param eventName The name of the event
     * @param params Optional parameters for the event
     */
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap())

    /**
     * Set a user property
     * @param name The property name
     * @param value The property value
     */
    fun setUserProperty(name: String, value: String)

    /**
     * Set the user ID for analytics
     * @param userId The user ID (can be null to clear)
     */
    fun setUserId(userId: String?)
}

/**
 * Common analytics event names
 */
object AnalyticsEvents {
    const val MOVIE_VIEWED = "movie_viewed"
    const val MOVIE_FAVORITED = "movie_favorited"
    const val MOVIE_UNFAVORITED = "movie_unfavorited"
    const val TV_SHOW_VIEWED = "tv_show_viewed"
    const val TV_SHOW_FAVORITED = "tv_show_favorited"
    const val TV_SHOW_UNFAVORITED = "tv_show_unfavorited"
    const val PERSON_VIEWED = "person_viewed"
    const val SEARCH_PERFORMED = "search_performed"
    const val THEME_CHANGED = "theme_changed"
}

/**
 * Common analytics parameter names
 */
object AnalyticsParams {
    const val ITEM_ID = "item_id"
    const val ITEM_NAME = "item_name"
    const val CONTENT_TYPE = "content_type"
    const val SEARCH_TERM = "search_term"
    const val THEME_MODE = "theme_mode"
}
