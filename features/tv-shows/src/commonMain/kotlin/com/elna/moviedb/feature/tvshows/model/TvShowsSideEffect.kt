package com.elna.moviedb.feature.tvshows.model

/**
 * Represents one-time side effects in the TV Shows screen.
 * These are events that should be consumed once (like showing a snackbar).
 */
sealed interface TvShowsSideEffect {
    /**
     * Show a pagination error snackbar with the given message
     */
    data class ShowPaginationError(val message: String) : TvShowsSideEffect
}
