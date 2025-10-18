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

/**
 * Remote data source for TMDB search API endpoints.
 *
 * This class follows the Open/Closed Principle by using a generic search method.
 * All search methods delegate to the private generic method, eliminating code duplication.
 */
class SearchRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatchers: AppDispatchers
) {

    /**
     * Generic search method following DRY principle.
     * All public search methods delegate to this private method.
     *
     * @param endpoint The API endpoint path (e.g., "search/multi")
     * @param query The search query string
     * @param page The page number for pagination
     * @param language The language code for results
     * @return AppResult containing the typed search results page
     */
    private suspend inline fun <reified T> search(
        endpoint: String,
        query: String,
        page: Int,
        language: String
    ): AppResult<T> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("$TMDB_BASE_URL$endpoint") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("query", query)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                        parameters.append("include_adult", "false")
                    }
                }.body<T>()
            }
        }
    }

    suspend fun searchMulti(
        query: String,
        page: Int,
        language: String
    ): AppResult<RemoteMultiSearchPage> =
        search("search/multi", query, page, language)

    suspend fun searchMovies(
        query: String,
        page: Int,
        language: String
    ): AppResult<RemoteSearchMoviesPage> =
        search("search/movie", query, page, language)

    suspend fun searchTvShows(
        query: String,
        page: Int,
        language: String
    ): AppResult<RemoteSearchTvShowsPage> =
        search("search/tv", query, page, language)

    suspend fun searchPeople(
        query: String,
        page: Int,
        language: String
    ): AppResult<RemoteSearchPeoplePage> =
        search("search/person", query, page, language)
}
