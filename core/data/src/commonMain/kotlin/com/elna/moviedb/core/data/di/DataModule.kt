package com.elna.moviedb.core.data.di

import com.elna.moviedb.core.data.LanguageChangeCoordinatorsInitializer
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.data.movies.MoviesRepositoryImpl
import com.elna.moviedb.core.data.person.PersonRepository
import com.elna.moviedb.core.data.person.PersonRepositoryImpl
import com.elna.moviedb.core.data.search.SearchRepository
import com.elna.moviedb.core.data.search.SearchRepositoryImpl
import com.elna.moviedb.core.data.tv_shows.TvShowRepositoryImpl
import com.elna.moviedb.core.data.tv_shows.TvShowsRepository
import com.elna.moviedb.core.data.util.LanguageProvider
import org.koin.dsl.module

val dataModule = module {

    single { LanguageProvider(get()) }

    single<MoviesRepository> {
        MoviesRepositoryImpl(
            moviesRemoteDataSource = get(),
            moviesLocalDataSource = get(),
            paginationPreferences = get(),
            languageProvider = get(),
        )
    }

    single<TvShowsRepository> {
        TvShowRepositoryImpl(
            tvShowsRemoteDataSource = get(),
            languageProvider = get(),
        )
    }

    single<SearchRepository> {
        SearchRepositoryImpl(
            searchRemoteDataSource = get(),
            languageProvider = get()
        )
    }

    single<PersonRepository> {
        PersonRepositoryImpl(
            personRemoteDataSource = get(),
            languageProvider = get()
        )
    }

    // Language change coordinators initializer
    single(createdAtStart = true) {
        LanguageChangeCoordinatorsInitializer(
            appSettingsPreferences = get(),
            appDispatchers = get(),
            moviesRepository = get(),
            tvShowsRepository = get()
        )
    }
}