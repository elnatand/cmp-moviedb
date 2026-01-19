package com.elna.moviedb.feature.tvshows.domain.repositories

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.tvshows.domain.model.TvShow
import com.elna.moviedb.feature.tvshows.domain.model.TvShowCategory
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for TV show data operations.
 */
interface TvShowsRepository {
    /**
     * Observes TV shows for a specific category from in-memory storage.
     *
     * Returns a flow of TV shows from the in-memory cache. Automatically triggers
     * initial load if cache is empty for the given category.
     *
     * @param category The TV show category to observe (e.g., POPULAR, ON_THE_AIR)
     * @return Flow emitting list of TV shows for the category
     */
    suspend fun observeTvShows(category: TvShowCategory): Flow<List<TvShow>>

    /**
     * Loads the next page of TV shows for a specific category from the remote API.
     *
     * @param category The TV show category to load (e.g., POPULAR, ON_THE_AIR)
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    suspend fun loadTvShowsNextPage(category: TvShowCategory): AppResult<Unit>

    /**
     * Retrieves detailed information for a specific TV show.
     *
     * @param tvShowId The unique identifier of the TV show
     * @return AppResult<TvShowDetails> Success with TV show details or Error if fetch failed
     */
    suspend fun getTvShowDetails(tvShowId: Int): AppResult<TvShowDetails>

    /**
     * Clears all cached TV shows and reloads initial pages for all categories.
     * Used by language coordinator when language changes.
     */
    suspend fun clearAndReload()
}