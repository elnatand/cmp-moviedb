package com.elna.moviedb.feature.movies.model

/**
 * Represents one-time UI actions in the Movies screen.
 * These are actions that the ViewModel requests the UI to perform once (like showing a snackbar).
 */
sealed interface MoviesUiAction {
    /**
     * Show a pagination error snackbar with the given message
     */
    data class ShowPaginationError(val message: String) : MoviesUiAction
}
