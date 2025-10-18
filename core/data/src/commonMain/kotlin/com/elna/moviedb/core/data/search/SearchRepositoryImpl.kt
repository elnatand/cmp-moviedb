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
 * Uses a category-based approach with SearchFilter enum. Each filter type is handled
 * inline within the main search method, eliminating duplicate private methods.
 *
 * Adding new search filters requires:
 * 1. Add filter to SearchFilter enum with apiPath
 * 2. Add corresponding method to SearchRemoteDataSource
 * 3. Add a when branch here to handle the new type
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

        val language = languageProvider.getCurrentLanguage()

        // Category-based search following OCP
        // Each filter delegates to the appropriate SearchRemoteDataSource method
        val result = when (filter) {
            SearchFilter.ALL -> {
                val remoteResult = searchRemoteDataSource.searchMulti(query, page, language)
                when (remoteResult) {
                    is AppResult.Success -> {
                        val searchItems = remoteResult.data.results.mapNotNull { multiSearchItem ->
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
                        AppResult.Success(searchItems)
                    }
                    is AppResult.Error -> AppResult.Error(remoteResult.message, throwable = remoteResult.throwable)
                }
            }

            SearchFilter.MOVIES -> {
                val remoteResult = searchRemoteDataSource.searchMovies(query, page, language)
                when (remoteResult) {
                    is AppResult.Success -> {
                        val movieItems = remoteResult.data.results.map { remoteSearchMovie ->
                            val searchResult = remoteSearchMovie.toSearchResult()
                            searchResult.copy(
                                movie = searchResult.movie.copy(
                                    posterPath = remoteSearchMovie.posterPath.toFullImageUrl()
                                ),
                                backdropPath = remoteSearchMovie.backdropPath.toFullImageUrl()
                            )
                        }
                        AppResult.Success(movieItems)
                    }
                    is AppResult.Error -> AppResult.Error(remoteResult.message, throwable = remoteResult.throwable)
                }
            }

            SearchFilter.TV_SHOWS -> {
                val remoteResult = searchRemoteDataSource.searchTvShows(query, page, language)
                when (remoteResult) {
                    is AppResult.Success -> {
                        val tvShowItems = remoteResult.data.results.map { remoteSearchTvShow ->
                            val searchResult = remoteSearchTvShow.toSearchResult()
                            searchResult.copy(
                                tvShow = searchResult.tvShow.copy(
                                    posterPath = remoteSearchTvShow.posterPath.toFullImageUrl()
                                ),
                                backdropPath = remoteSearchTvShow.backdropPath.toFullImageUrl()
                            )
                        }
                        AppResult.Success(tvShowItems)
                    }
                    is AppResult.Error -> AppResult.Error(remoteResult.message, throwable = remoteResult.throwable)
                }
            }

            SearchFilter.PEOPLE -> {
                val remoteResult = searchRemoteDataSource.searchPeople(query, page, language)
                when (remoteResult) {
                    is AppResult.Success -> {
                        val personItems = remoteResult.data.results.map { remoteSearchPerson ->
                            val searchResult = remoteSearchPerson.toSearchResult()
                            searchResult.copy(
                                profilePath = remoteSearchPerson.profilePath.toFullImageUrl()
                            )
                        }
                        AppResult.Success(personItems)
                    }
                    is AppResult.Error -> AppResult.Error(remoteResult.message, throwable = remoteResult.throwable)
                }
            }
        }

        emit(result)
    }
}