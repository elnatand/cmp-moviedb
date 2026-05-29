package com.elna.moviedb.feature.tvshows.presentation.model

/**
 * Represents one-time UI actions in the TV Shows screen.
 * These are actions that the ViewModel requests the UI to perform once (like showing a snackbar).
 */
sealed interface TvShowsUiAction {
    /**
     * Show a pagination error snackbar. The user-facing message is resolved from
     * localized string resources at the UI layer — never carried as a raw string.
     */
    data object ShowPaginationError : TvShowsUiAction
}
