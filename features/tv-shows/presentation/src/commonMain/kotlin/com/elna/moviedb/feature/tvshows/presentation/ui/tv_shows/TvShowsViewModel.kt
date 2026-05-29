package com.elna.moviedb.feature.tvshows.presentation.ui.tv_shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.elna.moviedb.feature.tvshows.domain.repositories.TvShowsRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.tvshows.domain.model.TvShowCategory
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsEvent
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsUiAction
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsUiState

/**
 * ViewModel following MVI (Model-View-Intent) pattern for TV Shows screen.
 * Implements Android's unidirectional data flow (UDF) pattern.
 *
 * UDF Components:
 * - Model: [TvShowsUiState] - Immutable state representing the UI
 * - View: TvShowsScreen - Renders the state and dispatches events
 * - Event: [TvShowsEvent] - User actions/events
 * - UI Action: [TvShowsUiAction] - One-time events (e.g., show snackbar)
 */
class TvShowsViewModel(
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TvShowsUiState(state = TvShowsUiState.State.LOADING))
    val uiState: StateFlow<TvShowsUiState> = _uiState.asStateFlow()

    private val _uiAction = Channel<TvShowsUiAction>(Channel.BUFFERED)
    val uiAction = _uiAction.receiveAsFlow()

    // Categories whose most recent load attempt failed. The full-screen error is only shown
    // when no category has data anywhere (see recomputeScreenState). Confined to
    // viewModelScope's main dispatcher, so it's never mutated concurrently.
    private val erroredCategories = mutableSetOf<TvShowCategory>()

    init {
        observeTvShows()
    }

    /**
     * Main entry point for handling user events.
     * All UI interactions should go through this method.
     */
    fun onEvent(event: TvShowsEvent) {
        when (event) {
            is TvShowsEvent.LoadNextPage -> loadNextPage(event.category)
            TvShowsEvent.Retry -> retry()
            TvShowsEvent.Refresh -> refresh()
        }
    }

    /**
     * Observes TV shows for all categories defined in [TvShowCategory] enum.
     *
     * This implementation is truly open for extension:
     * - Adding a new category to TvShowCategory enum requires ZERO changes here
     * - The map-based state automatically accommodates any number of categories
     */
    private fun observeTvShows() {
        TvShowCategory.entries.forEach { category ->
            viewModelScope.launch {
                var initialLoadRequested = false
                tvShowsRepository.observeTvShows(category).collect { tvShows ->
                    // Repository observe is passive; trigger the first load here (once)
                    // when the cache is empty. Keeps query/command responsibilities split.
                    // The flag latches on the FIRST emission regardless of its contents: a
                    // later empty emission (e.g. clearAndReload wiping the cache during
                    // pull-to-refresh) must not re-trigger a load here, which would race a
                    // redundant page fetch against refresh's own reload.
                    if (!initialLoadRequested) {
                        initialLoadRequested = true
                        if (tvShows.isEmpty()) loadNextPage(category)
                    }
                    _uiState.update { currentState ->
                        currentState.copy(
                            tvShowsByCategory = currentState.tvShowsByCategory + (category to tvShows)
                        )
                    }
                    // Overall screen state is derived, not set per-emission, so a single
                    // category can't clobber another category's state.
                    recomputeScreenState()
                }
            }
        }
    }

    /**
     * Loads the next page of TV shows for a specific category.
     *
     * Truly follows OCP - handles any category without code changes.
     * Implements proper error handling with different behaviors for initial vs pagination errors.
     *
     * This implementation is fully extensible:
     * - No when statements on category
     * - No hardcoded category checks
     * - Adding new categories requires ZERO changes to this method
     *
     * @param category The TV show category to load the next page for
     */
    private fun loadNextPage(category: TvShowCategory) {
        // Check if already loading for this category using map-based state
        if (_uiState.value.isLoading(category)) return

        viewModelScope.launch {
            setCategoryLoading(category, true)

            when (val result = tvShowsRepository.loadTvShowsNextPage(category)) {
                is AppResult.Error -> {
                    setCategoryLoading(category, false)
                    erroredCategories += category

                    // A failed load only blanks the screen when there's nothing to show
                    // anywhere. If any category already has data, this is a pagination /
                    // refresh hiccup — surface it as a snackbar and keep the content.
                    if (_uiState.value.hasAnyData) {
                        _uiAction.send(TvShowsUiAction.ShowPaginationError)
                    }
                    recomputeScreenState()
                }

                is AppResult.Success -> {
                    setCategoryLoading(category, false)
                    erroredCategories -= category
                    recomputeScreenState()
                    // Loaded TV shows arrive via observeTvShows().
                }
            }
        }
    }

    private fun setCategoryLoading(category: TvShowCategory, isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                loadingByCategory = currentState.loadingByCategory + (category to isLoading)
            )
        }
    }

    /**
     * Derives the overall screen state from the current data and error tracking.
     *
     * The full-screen error is shown only when a load has failed AND no category has any
     * data to display. As soon as any category has content, errors become non-blocking
     * (surfaced via snackbar), so one failing category never hides others' data.
     */
    private fun recomputeScreenState() {
        _uiState.update { currentState ->
            val newState = if (!currentState.hasAnyData && erroredCategories.isNotEmpty()) {
                TvShowsUiState.State.ERROR
            } else {
                TvShowsUiState.State.SUCCESS
            }
            // Mirror the per-category failures into state so a section that failed while
            // others have data renders an inline error + retry instead of a stuck loader.
            currentState.copy(state = newState, failedCategories = erroredCategories.toSet())
        }
    }

    /**
     * Retries loading TV shows for all categories after an error.
     *
     * Clears stale failures and lets each category reload independently. The screen state
     * (and any per-category snackbar) is then derived by loadNextPage.
     */
    private fun retry() {
        erroredCategories.clear()
        recomputeScreenState()
        TvShowCategory.entries.forEach { category -> loadNextPage(category) }
    }

    /**
     * Refreshes all TV show categories by clearing the cache and reloading.
     *
     * Triggered by the pull-to-refresh gesture. Mirrors the Movies refresh flow: keeps the
     * current content visible while reloading, and only surfaces an error (snackbar or
     * full-screen) when appropriate.
     */
    private fun refresh() {
        // Prevent duplicate refresh operations
        if (_uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            when (tvShowsRepository.clearAndReload()) {
                is AppResult.Error -> {
                    // The cache was just cleared. If the reload failed wholesale, record the
                    // failures so the derived screen state shows an error instead of spinning
                    // on empty per-section loaders forever.
                    erroredCategories.addAll(TvShowCategory.entries)
                    if (_uiState.value.hasAnyData) {
                        _uiAction.send(TvShowsUiAction.ShowPaginationError)
                    }
                }

                is AppResult.Success -> erroredCategories.clear()
            }
            recomputeScreenState()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}