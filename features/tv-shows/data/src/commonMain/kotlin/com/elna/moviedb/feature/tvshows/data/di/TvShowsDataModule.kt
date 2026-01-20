package com.elna.moviedb.feature.tvshows.data.di

import com.elna.moviedb.feature.tvshows.data.datasources.TvShowsRemoteService
import com.elna.moviedb.feature.tvshows.data.repositories.TvShowRepositoryImpl
import com.elna.moviedb.feature.tvshows.domain.repositories.TvShowsRepository
import org.koin.dsl.module

val tvShowsDataModule = module {

    // Remote data source - handles TV shows API calls
    single {
        TvShowsRemoteService(
            apiClient = get()
        )
    }

    single<TvShowsRepository> {
        TvShowRepositoryImpl(
            remoteDataSource = get(),
            languageProvider = get(),
            languageChangeCoordinator = get(),
        )
    }
}
