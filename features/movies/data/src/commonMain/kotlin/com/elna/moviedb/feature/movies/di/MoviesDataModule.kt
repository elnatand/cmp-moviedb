package com.elna.moviedb.feature.movies.di


import com.elna.moviedb.feature.movies.repositories.MoviesRepository
import com.elna.moviedb.feature.movies.repositories.MoviesRepositoryImpl
import org.koin.dsl.module

val moviesDataModule = module {

    single<MoviesRepository> {
        MoviesRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            paginationPreferences =  get(),
            languageProvider = get(),
            cachingStrategy = get(),
            languageChangeCoordinator = get(),
        )
    }
}