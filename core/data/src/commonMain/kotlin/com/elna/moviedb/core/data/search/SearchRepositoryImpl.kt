package com.elna.moviedb.core.data.search

import com.elna.moviedb.core.data.util.LanguageProvider
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchFilter
import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.core.network.SearchRemoteDataSource
import com.elna.moviedb.core.network.model.search.RemoteMultiSearchPage
import com.elna.moviedb.core.network.model.search.RemoteSearchMoviesPage
import com.elna.moviedb.core.network.model.search.RemoteSearchPeoplePage
import com.elna.moviedb.core.network.model.search.RemoteSearchTvShowsPage
import com.elna.moviedb.core.network.model.search.toSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of search repository following Open/Closed Principle.
 *
 * Uses a category-based approach with SearchFilter enum. Each filter type is handled
 * inline within the main search method using a unified search API.
 *
 * Adding new search filters requires:
 * 1. Add filter to SearchFilter enum (pure domain model)
 * 2. Add mapping in SearchFilterMapper (network layer)
 * 3. Add a when branch here to handle the new response type
 *
 * The SearchRemoteDataSource no longer needs changes - it uses a generic unified method!
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

        // Category-based search following OCP using unified search method
        // Each filter specifies the response type via reified generics
        val result = when (filter) {
            SearchFilter.ALL -> {
                val remoteResult = searchRemoteDataSource.search<RemoteMultiSearchPage>(
                    filter = filter,
                    query = query,
                    page = page,
                    language = language
                )
                when (remoteResult) {
                    is AppResult.Success -> {
                        val searchItems = remoteResult.data.results.mapNotNull { multiSearchItem ->
                            multiSearchItem.toSearchResult()
                        }
                        AppResult.Success(searchItems)
                    }
                    is AppResult.Error -> AppResult.Error(remoteResult.message, throwable = remoteResult.throwable)
                }
            }

            SearchFilter.MOVIES -> {
                val remoteResult = searchRemoteDataSource.search<RemoteSearchMoviesPage>(
                    filter = filter,
                    query = query,
                    page = page,
                    language = language
                )
                when (remoteResult) {
                    is AppResult.Success -> {
                        val movieItems = remoteResult.data.results.map { remoteSearchMovie ->
                            remoteSearchMovie.toSearchResult()
                        }
                        AppResult.Success(movieItems)
                    }
                    is AppResult.Error -> AppResult.Error(remoteResult.message, throwable = remoteResult.throwable)
                }
            }

            SearchFilter.TV_SHOWS -> {
                val remoteResult = searchRemoteDataSource.search<RemoteSearchTvShowsPage>(
                    filter = filter,
                    query = query,
                    page = page,
                    language = language
                )
                when (remoteResult) {
                    is AppResult.Success -> {
                        val tvShowItems = remoteResult.data.results.map { remoteSearchTvShow ->
                            remoteSearchTvShow.toSearchResult()
                        }
                        AppResult.Success(tvShowItems)
                    }
                    is AppResult.Error -> AppResult.Error(remoteResult.message, throwable = remoteResult.throwable)
                }
            }

            SearchFilter.PEOPLE -> {
                val remoteResult = searchRemoteDataSource.search<RemoteSearchPeoplePage>(
                    filter = filter,
                    query = query,
                    page = page,
                    language = language
                )
                when (remoteResult) {
                    is AppResult.Success -> {
                        val personItems = remoteResult.data.results.map { remoteSearchPerson ->
                            remoteSearchPerson.toSearchResult()
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