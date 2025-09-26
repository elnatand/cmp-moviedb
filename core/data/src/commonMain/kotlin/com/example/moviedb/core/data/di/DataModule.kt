package com.example.moviedb.core.data.di

import com.example.moviedb.core.common.DISPATCHER_IO
import com.example.moviedb.core.data.movies.MoviesRepository
import com.example.moviedb.core.data.movies.MoviesRepositoryImpl
import com.example.moviedb.core.data.movies.data_sources.MoviesLocalDataSource
import com.example.moviedb.core.data.movies.data_sources.MoviesRemoteDataSource
import com.example.moviedb.core.data.network.createHttpClient
import com.example.moviedb.core.data.tv_shows.TvShowRepositoryImpl
import com.example.moviedb.core.data.tv_shows.TvShowsRepository
import com.example.moviedb.core.data.tv_shows.data_sources.TvShowsRemoteDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single { createHttpClient(httpClientEngine = get()) }

    single {
        TvShowsRemoteDataSource(
            httpClient = get(),
            appDispatcher = get(named(DISPATCHER_IO))
        )
    }
    single<TvShowsRepository> { TvShowRepositoryImpl(tvShowsRemoteDataSource = get()) }

    single {
        MoviesRemoteDataSource(
            httpClient = get(),
            appDispatcher = get(named(DISPATCHER_IO))
        )
    }

    single {
        MoviesLocalDataSource(
            movieDao = get(),
            movieDetailsDao = get(),
            appDispatcher = get(named(DISPATCHER_IO))
        )
    }

    single<MoviesRepository> {
        MoviesRepositoryImpl(
            moviesRemoteDataSource = get(),
            moviesLocalDataSource = get()
        )
    }
}