package com.elna.moviedb.core.data.di

import com.elna.moviedb.core.data.LanguageChangeCoordinator
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.data.movies.MoviesRepositoryImpl
import com.elna.moviedb.core.data.person.PersonRepository
import com.elna.moviedb.core.data.person.PersonRepositoryImpl
import com.elna.moviedb.core.data.search.SearchRepository
import com.elna.moviedb.core.data.search.SearchRepositoryImpl
import com.elna.moviedb.core.data.strategy.CachingStrategy
import com.elna.moviedb.core.data.strategy.OfflineFirstCachingStrategy
import com.elna.moviedb.core.data.tv_shows.TvShowRepositoryImpl
import com.elna.moviedb.core.data.tv_shows.TvShowsRepository
import com.elna.moviedb.core.data.util.LanguageProvider
import org.koin.dsl.module

val dataModule = module {

    // Caching strategy - used by repositories to handle cache/network coordination
    single<CachingStrategy> { OfflineFirstCachingStrategy() }

    single { LanguageProvider(get()) }

    // Language change coordinator - uses Observer Pattern for loose coupling.
    // Created at startup so it begins observing language changes immediately,
    // independent of which repository (if any) is injected first.
    single(createdAtStart = true) {
        LanguageChangeCoordinator(
            appSettingsPreferences = get(),
            appDispatchers = get(),
        )
    }

    // Created at startup so the repository self-registers as a language-change
    // listener immediately. Otherwise a listener would only be wired up after the
    // user first navigates to its screen, silently missing earlier language changes.
    single<MoviesRepository>(createdAtStart = true) {
        MoviesRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            paginationPreferences = get(),
            languageProvider = get(),
            cachingStrategy = get(),
            languageChangeCoordinator = get(),
        )
    }

    single<TvShowsRepository>(createdAtStart = true) {
        TvShowRepositoryImpl(
            remoteDataSource = get(),
            languageProvider = get(),
            languageChangeCoordinator = get(),
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
}