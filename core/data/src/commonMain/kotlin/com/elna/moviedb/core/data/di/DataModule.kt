package com.elna.moviedb.core.data.di

import com.elna.moviedb.core.common.DISPATCHER_IO
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.data.movies.MoviesRepositoryImpl
import com.elna.moviedb.core.database.MoviesLocalDataSource
import com.elna.moviedb.core.data.tv_shows.TvShowRepositoryImpl
import com.elna.moviedb.core.data.tv_shows.TvShowsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    single<TvShowsRepository> { TvShowRepositoryImpl(tvShowsRemoteDataSource = get()) }

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