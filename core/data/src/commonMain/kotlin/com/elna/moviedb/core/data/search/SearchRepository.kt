package com.elna.moviedb.core.data.search

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchResultItem
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchMovies(query: String): Flow<AppResult<List<SearchResultItem.MovieItem>>>
    fun searchTvShows(query: String): Flow<AppResult<List<SearchResultItem.TvShowItem>>>
    fun searchAll(query: String): Flow<AppResult<List<SearchResultItem>>>
}