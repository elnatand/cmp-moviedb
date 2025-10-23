package com.elna.moviedb.core.network.utils

import com.elna.moviedb.core.model.AppResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException

@PublishedApi
internal suspend fun <T> safeApiCall(apiCall: suspend () -> T): AppResult<T> {
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
