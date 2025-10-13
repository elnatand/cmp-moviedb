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

    /**
     * Searches The Movie Database across movies, TV shows, and people using the provided query.
     *
     * @param query The search query string.
     * @param page The page number to retrieve (1-based).
     * @param language The language/locale code for results (e.g., "en-US").
     * @return An AppResult containing the retrieved RemoteMultiSearchPage on success, or an error AppResult on failure.
     */
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

    /**
     * Searches for movies matching the given query and returns a page of remote results.
     *
     * @param query The search query string.
     * @param page The page number to retrieve (1-based).
     * @param language The language code (ISO 639-1) for localized results.
     * @return An AppResult containing the RemoteSearchMoviesPage with the requested page of movie search results.
     */
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

    /**
     * Searches TV shows that match the provided text query.
     *
     * @param query The search text.
     * @param page The page number of results (1-based).
     * @param language The language/locale code for results (e.g., "en-US").
     * @return An AppResult containing a RemoteSearchTvShowsPage on success, or an error result on failure.
     */
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

    /**
     * Searches for people on TMDB matching the given query and returns a paginated result.
     *
     * Performs a paginated people search in the specified language.
     *
     * @param query The text to search for (person name or keyword).
     * @param page The page number to retrieve, starting at 1.
     * @param language The language for results, using TMDB language codes (e.g., "en-US" or "en").
     * @return An AppResult containing the RemoteSearchPeoplePage on success, or an error result on failure.
     */
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