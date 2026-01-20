package com.elna.moviedb.feature.movies.di


import com.elna.moviedb.feature.movies.datasources.MoviesRemoteDataSource
import com.elna.moviedb.feature.movies.repositories.CachingStrategy
import com.elna.moviedb.feature.movies.repositories.MoviesRepository
import com.elna.moviedb.feature.movies.repositories.MoviesRepositoryImpl
import com.elna.moviedb.feature.movies.repositories.OfflineFirstCachingStrategy
import org.koin.dsl.module

val moviesDataModule = module {

    // Remote data source - handles movie API calls
    single {
        MoviesRemoteDataSource(
            apiClient = get()
        )
    }

    // Caching strategy - used by repositories to handle cache/network coordination
    single<CachingStrategy> { OfflineFirstCachingStrategy() }

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