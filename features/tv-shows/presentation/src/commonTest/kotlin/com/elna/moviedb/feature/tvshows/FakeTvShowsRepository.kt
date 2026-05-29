package com.elna.moviedb.feature.tvshows

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.tvshows.domain.model.TvShow
import com.elna.moviedb.feature.tvshows.domain.model.TvShowCategory
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails
import com.elna.moviedb.feature.tvshows.domain.repositories.TvShowsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake implementation of [TvShowsRepository] for testing.
 * Provides controllable behavior for all repository operations.
 */
class FakeTvShowsRepository : TvShowsRepository {
    private val tvShowsFlows = mutableMapOf<TvShowCategory, MutableStateFlow<List<TvShow>>>()
    private val nextPageResults = mutableMapOf<TvShowCategory, AppResult<Unit>>()

    val loadNextPageCallCount = mutableMapOf<TvShowCategory, Int>()
    var clearAndReloadCallCount = 0
    var clearAndReloadDelay = 0L
    var clearAndReloadResult: AppResult<Unit> = AppResult.Success(Unit)

    /**
     * Optional suspension applied inside [loadTvShowsNextPage] after the call is counted.
     * Use a non-zero value to keep a load "in flight" so tests can verify that the
     * ViewModel's in-progress guard prevents duplicate concurrent loads.
     */
    var loadNextPageDelay = 0L

    /** Result returned by [getTvShowDetails]; configurable per test. */
    var detailsResult: AppResult<TvShowDetails> = AppResult.Error("Not configured")

    init {
        TvShowCategory.entries.forEach { category ->
            tvShowsFlows[category] = MutableStateFlow(emptyList())
            loadNextPageCallCount[category] = 0
        }
    }

    fun setTvShowsForCategory(category: TvShowCategory, tvShows: List<TvShow>) {
        tvShowsFlows[category]?.value = tvShows
    }

    fun setNextPageResult(category: TvShowCategory, result: AppResult<Unit>) {
        nextPageResults[category] = result
    }

    /**
     * Resets all interaction counters. Useful after constructing the ViewModel under test,
     * which triggers one initial load per empty category, so individual tests can assert
     * only the calls produced by their own actions.
     */
    fun resetCounters() {
        TvShowCategory.entries.forEach { loadNextPageCallCount[it] = 0 }
        clearAndReloadCallCount = 0
    }

    override fun observeTvShows(category: TvShowCategory): Flow<List<TvShow>> {
        return tvShowsFlows[category] ?: flowOf(emptyList())
    }

    override suspend fun loadTvShowsNextPage(category: TvShowCategory): AppResult<Unit> {
        loadNextPageCallCount[category] = (loadNextPageCallCount[category] ?: 0) + 1
        if (loadNextPageDelay > 0) {
            delay(loadNextPageDelay)
        }
        return nextPageResults[category] ?: AppResult.Success(Unit)
    }

    override suspend fun getTvShowDetails(tvShowId: Int): AppResult<TvShowDetails> {
        return detailsResult
    }

    override suspend fun clearAndReload(): AppResult<Unit> {
        clearAndReloadCallCount++
        // Mirror the real repository: clearing the cache makes the observed flows emit an
        // empty list before the reload repopulates them. A correct ViewModel must not treat
        // that transient empty emission as a fresh "cache is empty → load it" trigger (which
        // would race a redundant page fetch against this clearAndReload's own reload).
        tvShowsFlows.values.forEach { it.value = emptyList() }
        if (clearAndReloadDelay > 0) {
            delay(clearAndReloadDelay)
        }
        return clearAndReloadResult
    }
}
