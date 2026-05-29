package com.elna.moviedb.core.ui.utils

import kotlin.math.round

/**
 * Formats a raw vote average (e.g. 7.86) to a single decimal place (e.g. "7.9").
 *
 * Rounds (rather than truncating) to the nearest tenth, and uses [round] rather than
 * [String.format], which is unavailable in Kotlin/Native (iOS). Shared so movie, TV, and
 * search UIs render ratings identically.
 */
fun formatRating(rating: Double): String = "${round(rating * 10) / 10.0}"
