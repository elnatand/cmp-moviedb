package com.elna.moviedb.core.ui.utils

/**
 * Formats a raw vote average (e.g. 7.834) to a single decimal place (e.g. "7.8").
 *
 * Uses integer arithmetic rather than [String.format], which is unavailable in
 * Kotlin/Native (iOS). Shared so movie, TV, and search UIs render ratings identically.
 */
fun formatRating(rating: Double): String = "${(rating * 10).toInt() / 10.0}"
