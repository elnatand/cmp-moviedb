package com.elna.moviedb.core.data.tv_shows

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface TvShowsRepository {
    /**
     * Exposes pagination errors as a SharedFlow for UI consumption.
     * Emits error messages when loading additional pages fails.
     */
    val paginationErrors: SharedFlow<String>

    suspend fun observeAllTvShows(): Flow<AppResult<List<TvShow>>>
    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails
    suspend fun loadNextPage()
    suspend fun refresh(): AppResult<List<TvShow>>
}