package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.model.search.RemoteMultiSearchPage
import com.elna.moviedb.core.network.model.search.RemoteSearchMoviesPage
import com.elna.moviedb.core.network.model.search.RemoteSearchPeoplePage
import com.elna.moviedb.core.network.model.search.RemoteSearchTvShowsPage
import com.elna.moviedb.core.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

class SearchRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatchers: AppDispatchers
) {

    suspend fun searchMulti(
        query: String,
        page: Int,
        language: String
    ): AppResult<RemoteMultiSearchPage> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}search/multi") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("query", query)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                        parameters.append("include_adult", "false")
                    }
                }.body<RemoteMultiSearchPage>()
            }
        }
    }

    suspend fun searchMovies(
        query: String,
        page: Int,
        language: String
    ): AppResult<RemoteSearchMoviesPage> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}search/movie") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("query", query)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                        parameters.append("include_adult", "false")
                    }
                }.body<RemoteSearchMoviesPage>()
            }
        }
    }

    suspend fun searchTvShows(
        query: String,
        page: Int,
        language: String
    ): AppResult<RemoteSearchTvShowsPage> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}search/tv") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("query", query)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                        parameters.append("include_adult", "false")
                    }
                }.body<RemoteSearchTvShowsPage>()
            }
        }
    }

    suspend fun searchPeople(
        query: String,
        page: Int,
        language: String
    ): AppResult<RemoteSearchPeoplePage> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}search/person") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("query", query)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                        parameters.append("include_adult", "false")
                    }
                }.body<RemoteSearchPeoplePage>()
            }
        }
    }
}
