package com.elna.moviedb.feature.movies.di


import com.elna.moviedb.feature.movies.datasources.MoviesLocalDataSource
import com.elna.moviedb.feature.movies.datasources.MoviesLocalDataSourceImpl
import com.elna.moviedb.feature.movies.datasources.MoviesRemoteDataSource
import com.elna.moviedb.feature.movies.datasources.MoviesRemoteDataSourceImpl
import com.elna.moviedb.feature.movies.repositories.MoviesRepository
import com.elna.moviedb.feature.movies.repositories.MoviesRepositoryImpl
import org.koin.dsl.module

val moviesDataModule = module {

    // Remote data source - handles movie API calls
    single<MoviesRemoteDataSource> {
        MoviesRemoteDataSourceImpl(
            apiClient = get()
        )
    }

    single<MoviesRepository>(createdAtStart = true) {
        MoviesRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            paginationPreferences =  get(),
            languageProvider = get(),
            languageChangeCoordinator = get(),
        )
    }

    single<MoviesLocalDataSource> {
        MoviesLocalDataSourceImpl(
            movieDao = get(),
            movieDetailsDao = get(),
        )
    }
}