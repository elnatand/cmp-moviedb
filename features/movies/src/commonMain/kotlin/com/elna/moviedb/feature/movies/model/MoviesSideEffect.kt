package com.elna.moviedb.feature.movies.model

/**
 * Represents one-time side effects in the Movies screen.
 * These are events that should be consumed once (like showing a snackbar).
 */
sealed interface MoviesSideEffect {
    /**
     * Show a pagination error snackbar with the given message
     */
    data class ShowPaginationError(val message: String) : MoviesSideEffect
}
