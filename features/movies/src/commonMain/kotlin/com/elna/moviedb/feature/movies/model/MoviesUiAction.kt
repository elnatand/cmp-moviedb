package com.elna.moviedb.feature.movies.model

/**
 * Represents one-time UI actions in the Movies screen.
 * These are actions that the ViewModel requests the UI to perform once (like showing a snackbar).
 */
sealed interface MoviesUiAction {
    /**
     * Show a pagination error snackbar. The user-facing message is resolved from
     * localized string resources at the UI layer — never carried as a raw string.
     */
    data object ShowPaginationError : MoviesUiAction
}
