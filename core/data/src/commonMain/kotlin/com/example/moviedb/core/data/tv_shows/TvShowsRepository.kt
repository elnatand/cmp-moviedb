package com.example.moviedb.core.data.tv_shows

import com.example.moviedb.core.model.AppResult
import com.example.moviedb.core.model.TvShow
import com.example.moviedb.core.model.TvShowDetails
import kotlinx.coroutines.flow.Flow

interface TvShowsRepository {
    suspend fun observeAllTvShows(): Flow<AppResult<List<TvShow>>>
    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails
    suspend fun loadNextPage()
    suspend fun refresh(): AppResult<List<TvShow>>
}