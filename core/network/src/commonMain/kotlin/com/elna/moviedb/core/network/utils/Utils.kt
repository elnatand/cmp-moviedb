package com.elna.moviedb.core.network.utils

import com.elna.moviedb.core.model.AppResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException

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
