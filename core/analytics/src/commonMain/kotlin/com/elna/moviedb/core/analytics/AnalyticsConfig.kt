package com.elna.moviedb.core.analytics

/**
 * Platform-specific analytics configuration
 */
expect val analyticsEnabled: Boolean

/**
 * Get platform-specific analytics service instance
 */
expect fun createAnalyticsService(): AnalyticsService
