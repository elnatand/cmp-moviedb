package com.elna.moviedb.core.data.tv_shows

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowDetails
import kotlinx.coroutines.flow.Flow

interface TvShowsRepository {
    suspend fun observeAllTvShows(): Flow<List<TvShow>>
    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails
    suspend fun loadNextPage(): AppResult<Unit>
    suspend fun refresh(): AppResult<List<TvShow>>
}