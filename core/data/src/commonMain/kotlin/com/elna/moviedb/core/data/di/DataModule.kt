package com.elna.moviedb.core.data.di

import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.data.movies.MoviesRepositoryImpl
import com.elna.moviedb.core.data.person.PersonRepository
import com.elna.moviedb.core.data.person.PersonRepositoryImpl
import com.elna.moviedb.core.data.search.SearchRepository
import com.elna.moviedb.core.data.search.SearchRepositoryImpl
import com.elna.moviedb.core.data.tv_shows.TvShowRepositoryImpl
import com.elna.moviedb.core.data.tv_shows.TvShowsRepository
import org.koin.dsl.module

val dataModule = module {

    single<MoviesRepository> {
        MoviesRepositoryImpl(
            moviesRemoteDataSource = get(),
            moviesLocalDataSource = get(),
            preferencesManager = get(),
            appDispatchers = get()
        )
    }

    single<TvShowsRepository> {
        TvShowRepositoryImpl(
            tvShowsRemoteDataSource = get(),
            preferencesManager = get(),
            appDispatchers = get()
        )
    }

    single<SearchRepository> {
        SearchRepositoryImpl(
            searchRemoteDataSource = get(),
            preferencesManager = get()
        )
    }

    single<PersonRepository> {
        PersonRepositoryImpl(
            personRemoteDataSource = get(),
            preferencesManager = get()
        )
    }
}