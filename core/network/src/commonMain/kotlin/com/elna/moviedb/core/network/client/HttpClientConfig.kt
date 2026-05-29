package com.elna.moviedb.core.network.client

import com.elna.moviedb.core.network.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(engine: HttpClientEngineFactory<*>) = HttpClient(engine) {
    // Throw ClientRequestException (4xx) / ServerResponseException (5xx) on non-2xx
    // responses so safeApiCall can classify them into DataError.CLIENT / DataError.SERVER.
    // Without this, Ktor attempts to deserialize the error body and the failure is
    // misclassified as a generic DataError.NETWORK.
    expectSuccess = true

    install(ContentNegotiation) {
        json(Json {
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // Logging is gated behind a build flag because LogLevel.BODY logs the full
    // request URL, which includes the TMDB api_key query parameter. Leaving it on
    // would leak the key into release logs. Enable via -PenableNetworkLogging=true.
    if (BuildKonfig.ENABLE_NETWORK_LOGGING) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("http client log = $message")
                }
            }
            level = LogLevel.BODY
        }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 15000
        connectTimeoutMillis = 15000
        socketTimeoutMillis = 15000
    }
}
