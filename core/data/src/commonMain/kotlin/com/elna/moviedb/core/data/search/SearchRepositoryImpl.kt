package com.elna.moviedb.core.data.search

import com.elna.moviedb.core.data.util.LanguageProvider
import com.elna.moviedb.core.data.util.toFullImageUrl
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchFilter
import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.core.network.SearchRemoteDataSource
import com.elna.moviedb.core.network.model.search.toSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of search repository following Open/Closed Principle.
 *
 * This class uses a category-based approach to eliminate method duplication.
 * Adding new search categories only requires adding to the when expression,
 * not creating new methods or interfaces.
 */
class SearchRepositoryImpl(
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val languageProvider: LanguageProvider
) : SearchRepository {

    override fun search(
        filter: SearchFilter,
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem>>> = flow {
        if (query.isBlank()) {
            emit(AppResult.Success(emptyList()))
            return@flow
        }

        val result = when (filter) {
            SearchFilter.ALL -> searchAll(query, page)
            SearchFilter.MOVIES -> searchMovies(query, page)
            SearchFilter.TV_SHOWS -> searchTvShows(query, page)
            SearchFilter.PEOPLE -> searchPeople(query, page)
        }

        result.collect { emit(it) }
    }

    private fun searchMovies(
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem>>> = flow {
        val result = searchRemoteDataSource.searchMovies(query, page, languageProvider.getCurrentLanguage())
        when (result) {
            is AppResult.Success -> {
                val movieItems = result.data.results.map { remoteSearchMovie ->
                    val searchResult = remoteSearchMovie.toSearchResult()
                    searchResult.copy(
                        movie = searchResult.movie.copy(
                            posterPath = remoteSearchMovie.posterPath.toFullImageUrl()
                        ),
                        backdropPath = remoteSearchMovie.backdropPath.toFullImageUrl()
                    )
                }
                emit(AppResult.Success(movieItems))
            }

            is AppResult.Error -> {
                emit(AppResult.Error(message = result.message, throwable = result.throwable))
            }
        }
    }

    private fun searchTvShows(
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem>>> = flow {
        val result = searchRemoteDataSource.searchTvShows(query, page, languageProvider.getCurrentLanguage())
        when (result) {
            is AppResult.Success -> {
                val tvShowItems = result.data.results.map { remoteSearchTvShow ->
                    val searchResult = remoteSearchTvShow.toSearchResult()
                    searchResult.copy(
                        tvShow = searchResult.tvShow.copy(
                            posterPath = remoteSearchTvShow.posterPath.toFullImageUrl()
                        ),
                        backdropPath = remoteSearchTvShow.backdropPath.toFullImageUrl()
                    )
                }
                emit(AppResult.Success(tvShowItems))
            }

            is AppResult.Error -> {
                emit(AppResult.Error(message = result.message, throwable = result.throwable))
            }
        }
    }

    private fun searchPeople(
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem>>> = flow {
        val result = searchRemoteDataSource.searchPeople(query, page, languageProvider.getCurrentLanguage())
        when (result) {
            is AppResult.Success -> {
                val personItems = result.data.results.map { remoteSearchPerson ->
                    val searchResult = remoteSearchPerson.toSearchResult()
                    searchResult.copy(
                        profilePath = remoteSearchPerson.profilePath.toFullImageUrl()
                    )
                }
                emit(AppResult.Success(personItems))
            }

            is AppResult.Error -> {
                emit(AppResult.Error(message = result.message, throwable = result.throwable))
            }
        }
    }

    private fun searchAll(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> =
        flow {
            val result = searchRemoteDataSource.searchMulti(query, page, languageProvider.getCurrentLanguage())
            when (result) {
                is AppResult.Success -> {
                    val searchItems = result.data.results.mapNotNull { multiSearchItem ->
                        multiSearchItem.toSearchResult()?.let { searchResult ->
                            when (searchResult) {
                                is SearchResultItem.MovieItem -> searchResult.copy(
                                    movie = searchResult.movie.copy(
                                        posterPath = multiSearchItem.posterPath.toFullImageUrl()
                                    ),
                                    backdropPath = multiSearchItem.backdropPath.toFullImageUrl()
                                )
                                is SearchResultItem.TvShowItem -> searchResult.copy(
                                    tvShow = searchResult.tvShow.copy(
                                        posterPath = multiSearchItem.posterPath.toFullImageUrl()
                                    ),
                                    backdropPath = multiSearchItem.backdropPath.toFullImageUrl()
                                )
                                is SearchResultItem.PersonItem -> searchResult.copy(
                                    profilePath = multiSearchItem.profilePath.toFullImageUrl()
                                )
                            }
                        }
                    }
                    emit(AppResult.Success(searchItems))
                }

                is AppResult.Error -> {
                    emit(AppResult.Error(message = result.message, throwable = result.throwable))
                }
            }
        }
}