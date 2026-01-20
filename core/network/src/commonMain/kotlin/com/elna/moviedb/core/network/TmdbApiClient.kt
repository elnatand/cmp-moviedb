package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

/**
 * High-level TMDB API client that completely abstracts away HTTP implementation details.
 *
 * Feature modules depend only on this class, with no direct access to HttpClient,
 * Ktor utilities, or AppDispatchers.
 *
 * This provides complete isolation between network infrastructure and feature business logic.
 */
class TmdbApiClient(
    val httpClient: HttpClient,
    val appDispatchers: AppDispatchers
) {

    /**
     * Generic authenticated GET request to TMDB API.
     *
     * Automatically includes API key authentication.
     *
     * @param path API endpoint path (e.g., "/movie/popular")
     * @param queryParams Query parameters as pairs
     * @return AppResult containing deserialized response or error
     *
     * Example:
     * ```
     * apiGet<RemoteMoviesPage>(
     *     path = "/movie/popular",
     *     "page" to "1",
     *     "language" to "en"
     * )
     * ```
     */
    suspend inline fun <reified T> get(
        path: String,
        vararg queryParams: Pair<String, String>
    ): AppResult<T> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}${path}") {
                    url {
                        // Always include API key
                        parameters.append("api_key", BuildKonfig.TMDB_API_KEY)

                        // Add additional query parameters
                        queryParams.forEach { (key, value) ->
                            parameters.append(key, value)
                        }
                    }
                }.body<T>()
            }
        }
    }
}
