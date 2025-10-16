package com.elna.moviedb.core.data.util

/**
 * Converts a relative image path to a full TMDB image URL.
 *
 * @receiver String? The relative path from TMDB API (e.g., "/abc123.jpg")
 * @return String? The full image URL or null if the path is null
 *
 * Example:
 * ```
 * val relativePath = "/abc123.jpg"
 * val fullUrl = relativePath.toFullImageUrl() // "https://image.tmdb.org/t/p/w500/abc123.jpg"
 * ```
 */

// Using w500 for high-quality images on high-density displays
// Optimal for 140dp wide cards (140dp × 3 density = 420px, w500 provides 500px)
private const val TMDB_IMAGE_URL = "https://media.themoviedb.org/t/p/w500"

internal fun String?.toFullImageUrl(): String? {
    return this?.let { "$TMDB_IMAGE_URL$it" }
}
