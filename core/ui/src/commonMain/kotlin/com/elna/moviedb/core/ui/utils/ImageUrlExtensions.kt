package com.elna.moviedb.core.ui.utils

/**
 * TMDB image size options for different use cases.
 * Choose the appropriate size based on the display requirements.
 *
 * Reference: https://developer.themoviedb.org/docs/image-basics
 */
enum class ImageSize(val path: String) {
    /**
     * Small thumbnail (92px width) - for list items on small screens
     */
    W92("w92"),

    /**
     * Medium thumbnail (154px width) - for grid items
     */
    W154("w154"),

    /**
     * Medium size (185px width) - for standard list/grid items
     */
    W185("w185"),

    /**
     * Large thumbnail (342px width) - for featured items
     */
    W342("w342"),

    /**
     * High quality (500px width) - for detail screens, high-density displays
     * Optimal for 140dp wide cards (140dp × 3 density = 420px, w500 provides 500px)
     */
    W500("w500"),

    /**
     * Extra high quality (780px width) - for large detail views
     */
    W780("w780"),

    /**
     * Original size - full resolution image
     */
    ORIGINAL("original")
}

/**
 * Converts a relative TMDB image path to a full image URL.
 *
 * This extension should be used in the presentation layer (screens, composables)
 * to transform domain model paths into displayable URLs.
 *
 * @receiver String? The relative path from TMDB API (e.g., "/abc123.jpg")
 * @param size The image size to use (defaults to W500 for high-quality display)
 * @return String The full image URL, or empty string if the path is null
 *
 * Example:
 * ```kotlin
 * // In a Composable
 * val posterUrl = movie.posterPath.toImageUrl(ImageSize.W500)
 *
 * AsyncImage(
 *     model = posterUrl,
 *     contentDescription = movie.title
 * )
 * ```
 */
fun String?.toImageUrl(size: ImageSize = ImageSize.W500): String {
    if (this.isNullOrBlank()) return ""
    return "https://media.themoviedb.org/t/p/${size.path}$this"
}

/**
 * Converts a relative TMDB poster path to a full poster URL.
 * Uses W500 size which is optimal for poster displays.
 *
 * @receiver String? The relative poster path
 * @return String The full poster URL, or empty string if the path is null
 */
fun String?.toPosterUrl(): String = toImageUrl(ImageSize.W500)

/**
 * Converts a relative TMDB backdrop path to a full backdrop URL.
 * Uses W780 size which is optimal for wide backdrop images.
 *
 * @receiver String? The relative backdrop path
 * @return String The full backdrop URL, or empty string if the path is null
 */
fun String?.toBackdropUrl(): String = toImageUrl(ImageSize.W780)

/**
 * Converts a relative TMDB profile path to a full profile URL.
 * Uses W185 size which is optimal for profile pictures.
 *
 * @receiver String? The relative profile path
 * @return String The full profile URL, or empty string if the path is null
 */
fun String?.toProfileUrl(): String = toImageUrl(ImageSize.W185)
