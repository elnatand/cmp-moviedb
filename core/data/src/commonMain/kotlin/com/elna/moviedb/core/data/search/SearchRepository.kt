package com.elna.moviedb.core.data.search

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchResultItem
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchMovies(query: String, page: Int): Flow<AppResult<List<SearchResultItem.MovieItem>>>
    fun searchTvShows(query: String, page: Int): Flow<AppResult<List<SearchResultItem.TvShowItem>>>
    fun searchPeople(query: String, page: Int): Flow<AppResult<List<SearchResultItem.PersonItem>>>
    fun searchAll(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>>
}