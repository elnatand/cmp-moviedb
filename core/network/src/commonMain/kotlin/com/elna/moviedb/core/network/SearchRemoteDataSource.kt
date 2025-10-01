package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatcher
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.model.platformCountry
import com.elna.moviedb.core.network.model.platformLanguage
import com.elna.moviedb.core.network.model.search.RemoteMultiSearchPage
import com.elna.moviedb.core.network.model.search.RemoteSearchMoviesPage
import com.elna.moviedb.core.network.model.search.RemoteSearchTvShowsPage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

class SearchRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatcher: AppDispatcher
) {
    private val language = "$platformLanguage-$platformCountry"

    suspend fun searchMulti(query: String, page: Int): AppResult<RemoteMultiSearchPage> {
        return try {
            val searchResults = withContext(appDispatcher.getDispatcher()) {
                httpClient
                    .get("${TMDB_BASE_URL}search/multi") {
                        url {
                            parameters.append("api_key", TMDB_API_KEY)
                            parameters.append("query", query)
                            parameters.append("page", page.toString())
                            parameters.append("language", language)
                            parameters.append("include_adult", "false")
                        }
                    }.body<RemoteMultiSearchPage>()
            }
            AppResult.Success(searchResults)
        } catch (e: Exception) {
            AppResult.Error(
                message = e.message ?: "Unknown error occurred",
                throwable = e
            )
        }
    }

    suspend fun searchMovies(query: String, page: Int): AppResult<RemoteSearchMoviesPage> {
        return try {
            val searchResults = withContext(appDispatcher.getDispatcher()) {
                httpClient
                    .get("${TMDB_BASE_URL}search/movie") {
                        url {
                            parameters.append("api_key", TMDB_API_KEY)
                            parameters.append("query", query)
                            parameters.append("page", page.toString())
                            parameters.append("language", language)
                            parameters.append("include_adult", "false")
                        }
                    }.body<RemoteSearchMoviesPage>()
            }
            AppResult.Success(searchResults)
        } catch (e: Exception) {
            AppResult.Error(
                message = e.message ?: "Unknown error occurred",
                throwable = e
            )
        }
    }

    suspend fun searchTvShows(query: String, page: Int): AppResult<RemoteSearchTvShowsPage> {
        return try {
            val searchResults = withContext(appDispatcher.getDispatcher()) {
                httpClient
                    .get("${TMDB_BASE_URL}search/tv") {
                        url {
                            parameters.append("api_key", TMDB_API_KEY)
                            parameters.append("query", query)
                            parameters.append("page", page.toString())
                            parameters.append("language", language)
                            parameters.append("include_adult", "false")
                        }
                    }.body<RemoteSearchTvShowsPage>()
            }
            AppResult.Success(searchResults)
        } catch (e: Exception) {
            AppResult.Error(
                message = e.message ?: "Unknown error occurred",
                throwable = e
            )
        }
    }
}