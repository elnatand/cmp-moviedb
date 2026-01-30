package com.elna.moviedb.core.network.di


import com.elna.moviedb.core.network.MoviesRemoteDataSource
import com.elna.moviedb.core.network.PersonRemoteDataSource
import com.elna.moviedb.core.network.SearchRemoteDataSource
import com.elna.moviedb.core.network.TvShowsRemoteDataSource
import com.elna.moviedb.core.network.client.provideHttpClientEngine
import com.elna.moviedb.core.network.client.createHttpClient
import org.koin.dsl.module

val networkModule = module {
    single { provideHttpClientEngine() }
    single { createHttpClient(get()) }

    single {
        TvShowsRemoteDataSource(
            httpClient = get(),
            appDispatchers = get()
        )
    }

    single {
        MoviesRemoteDataSource(
            httpClient = get(),
            appDispatchers = get()
        )
    }

    single {
        SearchRemoteDataSource(
            httpClient = get(),
            appDispatchers = get()
        )
    }

    single {
        PersonRemoteDataSource(
            httpClient = get(),
            appDispatchers = get()
        )
    }
}