package com.elna.moviedb.core.data.search

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.core.network.SearchRemoteDataSource
import com.elna.moviedb.core.network.model.search.toSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val searchRemoteDataSource: SearchRemoteDataSource
) : SearchRepository {

    override fun searchMovies(query: String, page: Int): Flow<AppResult<List<SearchResultItem.MovieItem>>> = flow {
        if (query.isBlank()) {
            emit(AppResult.Success(emptyList()))
            return@flow
        }

        val result = searchRemoteDataSource.searchMovies(query, page)
        when (result) {
            is AppResult.Success -> {
                val movieItems = result.data.results.map { it.toSearchResult() }
                emit(AppResult.Success(movieItems))
            }
            is AppResult.Error -> {
                emit(AppResult.Error(message = result.message, throwable = result.throwable))
            }
        }
    }

    override fun searchTvShows(query: String, page: Int): Flow<AppResult<List<SearchResultItem.TvShowItem>>> = flow {
        if (query.isBlank()) {
            emit(AppResult.Success(emptyList()))
            return@flow
        }

        val result = searchRemoteDataSource.searchTvShows(query, page)
        when (result) {
            is AppResult.Success -> {
                val tvShowItems = result.data.results.map { it.toSearchResult() }
                emit(AppResult.Success(tvShowItems))
            }
            is AppResult.Error -> {
                emit(AppResult.Error(message = result.message, throwable = result.throwable))
            }
        }
    }

    override fun searchAll(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> = flow {
        if (query.isBlank()) {
            emit(AppResult.Success(emptyList()))
            return@flow
        }

        val result = searchRemoteDataSource.searchMulti(query, page)
        when (result) {
            is AppResult.Success -> {
                val searchItems = result.data.results.mapNotNull { it.toSearchResult() }
                emit(AppResult.Success(searchItems))
            }
            is AppResult.Error -> {
                emit(AppResult.Error(message = result.message, throwable = result.throwable))
            }
        }
    }
}