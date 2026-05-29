package com.elna.moviedb.core.network.utils

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.DataError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException

/**
 * Wraps API calls with error handling - shared across all feature remote data sources.
 *
 * Failures are classified into a [DataError] so the presentation layer can show a
 * localized message; the raw [AppResult.Error.message] is retained for logging only.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(apiCall())
    } catch (e: ClientRequestException) {
        val statusCode = e.response.status.value
        val message = "Client error (${statusCode}): ${e.message}"
        // TODO: Add logger.error("API client error", e)
        AppResult.Error(message = message, code = statusCode, throwable = e, type = DataError.CLIENT)
    } catch (e: ServerResponseException) {
        val statusCode = e.response.status.value
        val message = "Server error (${statusCode}): ${e.message}"
        // TODO: Add logger.error("API server error", e)
        AppResult.Error(message = message, code = statusCode, throwable = e, type = DataError.SERVER)
    } catch (e: Exception) {
        val message = "Network error: ${e.message ?: "Unknown error occurred"}"
        // TODO: Add logger.error("API network error", e)
        AppResult.Error(message = message, throwable = e, type = DataError.NETWORK)
    }
}
