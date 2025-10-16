package com.elna.moviedb.core.data.search

import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.core.network.SearchRemoteDataSource
import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL
import com.elna.moviedb.core.network.model.search.toSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val preferencesManager: PreferencesManager
) : SearchRepository {

    override fun searchMovies(
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem.MovieItem>>> = flow {
        if (query.isBlank()) {
            emit(AppResult.Success(emptyList()))
            return@flow
        }

        val result = searchRemoteDataSource.searchMovies(query, page, getLanguage())
        when (result) {
            is AppResult.Success -> {
                val movieItems = result.data.results.map { remoteSearchMovie ->
                    val searchResult = remoteSearchMovie.toSearchResult()
                    searchResult.copy(
                        movie = searchResult.movie.copy(
                            posterPath = remoteSearchMovie.posterPath?.let { "$TMDB_IMAGE_URL$it" }
                        ),
                        backdropPath = remoteSearchMovie.backdropPath?.let { "$TMDB_IMAGE_URL$it" }
                    )
                }
                emit(AppResult.Success(movieItems))
            }

            is AppResult.Error -> {
                emit(AppResult.Error(message = result.message, throwable = result.throwable))
            }
        }
    }

    override fun searchTvShows(
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem.TvShowItem>>> = flow {
        if (query.isBlank()) {
            emit(AppResult.Success(emptyList()))
            return@flow
        }

        val result = searchRemoteDataSource.searchTvShows(query, page, getLanguage())
        when (result) {
            is AppResult.Success -> {
                val tvShowItems = result.data.results.map { remoteSearchTvShow ->
                    val searchResult = remoteSearchTvShow.toSearchResult()
                    searchResult.copy(
                        tvShow = searchResult.tvShow.copy(
                            posterPath = remoteSearchTvShow.posterPath?.let { "$TMDB_IMAGE_URL$it" }
                        ),
                        backdropPath = remoteSearchTvShow.backdropPath?.let { "$TMDB_IMAGE_URL$it" }
                    )
                }
                emit(AppResult.Success(tvShowItems))
            }

            is AppResult.Error -> {
                emit(AppResult.Error(message = result.message, throwable = result.throwable))
            }
        }
    }

    override fun searchPeople(
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem.PersonItem>>> = flow {
        if (query.isBlank()) {
            emit(AppResult.Success(emptyList()))
            return@flow
        }

        val result = searchRemoteDataSource.searchPeople(query, page, getLanguage())
        when (result) {
            is AppResult.Success -> {
                val personItems = result.data.results.map { remoteSearchPerson ->
                    val searchResult = remoteSearchPerson.toSearchResult()
                    searchResult.copy(
                        profilePath = remoteSearchPerson.profilePath?.let { "$TMDB_IMAGE_URL$it" }
                    )
                }
                emit(AppResult.Success(personItems))
            }

            is AppResult.Error -> {
                emit(AppResult.Error(message = result.message, throwable = result.throwable))
            }
        }
    }

    override fun searchAll(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> =
        flow {
            if (query.isBlank()) {
                emit(AppResult.Success(emptyList()))
                return@flow
            }

            val result = searchRemoteDataSource.searchMulti(query, page, getLanguage())
            when (result) {
                is AppResult.Success -> {
                    val searchItems = result.data.results.mapNotNull { multiSearchItem ->
                        multiSearchItem.toSearchResult()?.let { searchResult ->
                            when (searchResult) {
                                is SearchResultItem.MovieItem -> searchResult.copy(
                                    movie = searchResult.movie.copy(
                                        posterPath = multiSearchItem.posterPath?.let { "$TMDB_IMAGE_URL$it" }
                                    ),
                                    backdropPath = multiSearchItem.backdropPath?.let { "$TMDB_IMAGE_URL$it" }
                                )
                                is SearchResultItem.TvShowItem -> searchResult.copy(
                                    tvShow = searchResult.tvShow.copy(
                                        posterPath = multiSearchItem.posterPath?.let { "$TMDB_IMAGE_URL$it" }
                                    ),
                                    backdropPath = multiSearchItem.backdropPath?.let { "$TMDB_IMAGE_URL$it" }
                                )
                                is SearchResultItem.PersonItem -> searchResult.copy(
                                    profilePath = multiSearchItem.profilePath?.let { "$TMDB_IMAGE_URL$it" }
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

    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}