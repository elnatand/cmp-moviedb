package com.elna.moviedb.core.network.di


import com.elna.moviedb.core.network.TmdbApiClient
import com.elna.moviedb.core.network.ktor.createHttpClient
import org.koin.dsl.module

/**
 * Network module that provides shared HTTP infrastructure.
 *
 * Provides TmdbApiClient as the single entry point for all API calls.
 * Feature modules depend only on TmdbApiClient, with no direct access to
 * HttpClient, Ktor, or AppDispatchers.
 */
val networkModule = module {
    // Internal HTTP client - not exposed to features
    single { createHttpClient(httpClientEngine = get()) }

    // Public API client - the only network dependency features should use
    single {
        TmdbApiClient(
            httpClient = get(),
            appDispatchers = get()
        )
    }
}