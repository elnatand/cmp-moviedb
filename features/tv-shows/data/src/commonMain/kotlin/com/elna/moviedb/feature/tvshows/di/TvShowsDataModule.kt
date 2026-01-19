package com.elna.moviedb.feature.tvshows.di

import com.elna.moviedb.feature.tvshows.data.TvShowRepositoryImpl
import com.elna.moviedb.feature.tvshows.repositories.TvShowsRepository
import org.koin.dsl.module

val tvShowsDataModule = module {
    single<TvShowsRepository> {
        TvShowRepositoryImpl(
            remoteDataSource = get(),
            languageProvider = get(),
            languageChangeCoordinator = get(),
        )
    }
}
