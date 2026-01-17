package com.elna.moviedb.feature.tvshows.model

/**
 * Represents one-time UI actions in the TV Shows screen.
 * These are actions that the ViewModel requests the UI to perform once (like showing a snackbar).
 */
sealed interface TvShowsUiAction {
    /**
     * Show a pagination error snackbar with the given message
     */
    data class ShowPaginationError(val message: String) : TvShowsUiAction
}
