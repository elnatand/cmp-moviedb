package com.elna.moviedb.core.data.tv_shows

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowDetails
import kotlinx.coroutines.flow.Flow

interface TvShowsRepository {
    suspend fun observePopularTvShows(): Flow<List<TvShow>>
    suspend fun observeOnTheAirTvShows(): Flow<List<TvShow>>
    suspend fun observeTopRatedTvShows(): Flow<List<TvShow>>
    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails
    suspend fun loadPopularTvShowsNextPage(): AppResult<Unit>
    suspend fun loadOnTheAirTvShowsNextPage(): AppResult<Unit>
    suspend fun loadTopRatedTvShowsNextPage(): AppResult<Unit>
    suspend fun refresh(): AppResult<List<TvShow>>
}