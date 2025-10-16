package com.elna.moviedb.core.data.util

import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL

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
fun String?.toFullImageUrl(): String? {
    return this?.let { "$TMDB_IMAGE_URL$it" }
}
