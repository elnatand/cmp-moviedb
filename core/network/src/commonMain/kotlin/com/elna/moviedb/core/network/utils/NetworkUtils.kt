package com.elna.moviedb.core.network.utils

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.BuildKonfig
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

/**
 * Wraps API calls with error handling - shared across all feature remote data sources
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(apiCall())
    } catch (e: ClientRequestException) {
        val statusCode = e.response.status.value
        val message = "Client error (${statusCode}): ${e.message}"
        // TODO: Add logger.error("API client error", e)
        AppResult.Error(message = message, throwable = e)
    } catch (e: ServerResponseException) {
        val statusCode = e.response.status.value
        val message = "Server error (${statusCode}): ${e.message}"
        // TODO: Add logger.error("API server error", e)
        AppResult.Error(message = message, throwable = e)
    } catch (e: Exception) {
        val message = "Network error: ${e.message ?: "Unknown error occurred"}"
        // TODO: Add logger.error("API network error", e)
        AppResult.Error(message = message, throwable = e)
    }
}

/**
 * Generic API GET request function that abstracts away Ktor details.
 *
 * Feature modules can use this without depending on HttpClient or Ktor utilities.
 *
 * @param httpClient The HTTP client instance (injected via DI)
 * @param appDispatchers Coroutine dispatchers
 * @param path API endpoint path (e.g., "/movie/popular")
 * @param queryParams Query parameters as pairs (e.g., "page" to "1", "language" to "en")
 * @return AppResult containing deserialized response or error
 *
 * Example:
 * ```
 * apiGet<RemoteMoviesPage>(
 *     httpClient = httpClient,
 *     appDispatchers = appDispatchers,
 *     path = "/movie/popular",
 *     queryParams = arrayOf(
 *         "api_key" to BuildKonfig.TMDB_API_KEY,
 *         "page" to "1",
 *         "language" to "en"
 *     )
 * )
 * ```
 */
suspend inline fun <reified T> apiGet(
    httpClient: HttpClient,
    appDispatchers: AppDispatchers,
    path: String,
    vararg queryParams: Pair<String, String>
): AppResult<T> {
    return withContext(appDispatchers.io) {
        safeApiCall {
            httpClient.get("${TMDB_BASE_URL}${path}") {
                url {
                    queryParams.forEach { (key, value) ->
                        parameters.append(key, value)
                    }
                }
            }.body<T>()
        }
    }
}

/**
 * Convenience function that automatically includes API key.
 *
 * @param httpClient The HTTP client instance (injected via DI)
 * @param appDispatchers Coroutine dispatchers
 * @param path API endpoint path
 * @param additionalParams Additional query parameters (page, language, etc.)
 */
suspend inline fun <reified T> apiGetWithAuth(
    httpClient: HttpClient,
    appDispatchers: AppDispatchers,
    path: String,
    vararg additionalParams: Pair<String, String>
): AppResult<T> {
    return apiGet(
        httpClient = httpClient,
        appDispatchers = appDispatchers,
        path = path,
        queryParams = arrayOf(
            "api_key" to BuildKonfig.TMDB_API_KEY,
            *additionalParams
        )
    )
}
