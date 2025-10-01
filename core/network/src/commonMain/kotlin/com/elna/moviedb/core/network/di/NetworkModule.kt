package com.elna.moviedb.core.network.di


import com.elna.moviedb.core.common.DISPATCHER_IO
import com.elna.moviedb.core.network.MoviesRemoteDataSource
import com.elna.moviedb.core.network.SearchRemoteDataSource
import com.elna.moviedb.core.network.TvShowsRemoteDataSource
import com.elna.moviedb.core.network.ktor.createHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient(httpClientEngine = get()) }

    single {
        TvShowsRemoteDataSource(
            httpClient = get(),
            appDispatcher = get(named(DISPATCHER_IO))
        )
    }

    single {
        MoviesRemoteDataSource(
            httpClient = get(),
            appDispatcher = get(named(DISPATCHER_IO))
        )
    }

    single {
        SearchRemoteDataSource(
            httpClient = get(),
            appDispatcher = get(named(DISPATCHER_IO))
        )
    }
}