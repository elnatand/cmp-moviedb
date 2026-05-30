package com.elna.moviedb.core.network.utils

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.DataError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Wraps API calls with error handling - shared across all feature remote data sources.
 *
 * Failures are classified into a [DataError] so the presentation layer can show a
 * localized message. The raw [AppResult.Error.message]/[AppResult.Error.throwable] carry
 * the technical detail for diagnostics (e.g. when debugging or wiring up logging); the UI
 * never displays them.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(apiCall())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ClientRequestException) {
        val statusCode = e.response.status.value
        val message = "Client error (${statusCode}): ${e.message}"
        AppResult.Error(message = message, code = statusCode, throwable = e, type = DataError.CLIENT)
    } catch (e: ServerResponseException) {
        val statusCode = e.response.status.value
        val message = "Server error (${statusCode}): ${e.message}"
        AppResult.Error(message = message, code = statusCode, throwable = e, type = DataError.SERVER)
    } catch (e: SerializationException) {
        // Response arrived but couldn't be deserialized (schema mismatch, malformed body).
        // This is not a connectivity problem, so classify it as UNKNOWN rather than NETWORK.
        val message = "Deserialization error: ${e.message ?: "Unknown error occurred"}"
        AppResult.Error(message = message, throwable = e, type = DataError.UNKNOWN)
    } catch (e: IOException) {
        // Genuine connectivity / IO failure (timeouts and socket errors extend IOException).
        val message = "Network error: ${e.message ?: "Unknown error occurred"}"
        AppResult.Error(message = message, throwable = e, type = DataError.NETWORK)
    } catch (e: Exception) {
        // Anything else (e.g. an unexpected IllegalStateException) is not connectivity-related,
        // so don't tell the user to "check your connection" — classify it as UNKNOWN.
        val message = "Unexpected error: ${e.message ?: "Unknown error occurred"}"
        AppResult.Error(message = message, throwable = e, type = DataError.UNKNOWN)
    }
}
