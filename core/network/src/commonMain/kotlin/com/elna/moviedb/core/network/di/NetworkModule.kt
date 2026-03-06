package com.elna.moviedb.core.network.di


import com.elna.moviedb.core.network.MoviesRemoteDataSource
import com.elna.moviedb.core.network.PersonRemoteDataSource
import com.elna.moviedb.core.network.SearchRemoteDataSource
import com.elna.moviedb.core.network.TvShowsRemoteDataSource
import com.elna.moviedb.core.network.client.provideHttpClientEngine
import com.elna.moviedb.core.network.client.createHttpClient
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
    single { provideHttpClientEngine() }
    single { createHttpClient(get()) }
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