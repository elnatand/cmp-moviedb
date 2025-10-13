package com.elna.moviedb.core.data.tv_shows

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowDetails
import kotlinx.coroutines.flow.Flow

interface TvShowsRepository {
    suspend fun observePopularTvShows(): Flow<List<TvShow>>
    suspend fun observeOnTheAirTvShows(): Flow<List<TvShow>>
    suspend fun observeTopRatedTvShows(): Flow<List<TvShow>>
    /**
 * Load the next page of popular TV shows and append it to the repository's stored list.
 *
 * @return `AppResult<Unit>` representing success with no value or an error containing failure details.
 */
suspend fun loadPopularTvShowsNextPage(): AppResult<Unit>
    /**
 * Loads the next page of currently airing TV shows into the repository.
 *
 * @return An AppResult containing `Unit` on success, or an error describing the failure.
 */
suspend fun loadOnTheAirTvShowsNextPage(): AppResult<Unit>
    /**
 * Loads the next page of top-rated TV shows into the repository's data set.
 *
 * Requests the next page of top-rated TV shows and integrates the results into the repository's stored data, advancing pagination state.
 *
 * @return An AppResult containing `Unit` on success, or an error describing the failure.
 */
suspend fun loadTopRatedTvShowsNextPage(): AppResult<Unit>
    /**
 * Fetches detailed information for a TV show identified by its ID.
 *
 * @param tvShowId The ID of the TV show to retrieve details for.
 * @return An AppResult containing the TvShowDetails on success, or an error describing the failure.
 */
suspend fun getTvShowDetails(tvShowId: Int): AppResult<TvShowDetails>
    /**
 * Refreshes the repository's TV show data.
 *
 * @return An AppResult containing the refreshed list of TvShow on success, or an error describing the failure.
 */
suspend fun refresh(): AppResult<List<TvShow>>
}