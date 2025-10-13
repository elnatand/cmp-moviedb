package com.elna.moviedb.core.network.utils

import com.elna.moviedb.core.model.AppResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException

/**
 * Executes the provided suspend API call and wraps its outcome in an AppResult, mapping common HTTP and other exceptions to standardized error results.
 *
 * @param apiCall A suspend function that performs the API request and returns a value of type `T`.
 * @return An AppResult containing `AppResult.Success` with the API result on success, or `AppResult.Error` with an explanatory message and the underlying throwable for 4xx, 5xx, or other exceptions.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(apiCall())
    } catch (e: ClientRequestException) {
        // 4xx errors, such as 404 Not Found
        AppResult.Error(message = "4xx errors", throwable = e)
    } catch (e: ServerResponseException) {
        // 5xx errors
        AppResult.Error(message = "5xx errors", throwable = e)
    } catch (e: Exception) {
        // Timeout, network, JSON parsing, etc.
        AppResult.Error(message = "Unknown error occurred", throwable = e)
    }
}