package com.elna.moviedb.core.database.di

import com.elna.moviedb.core.database.MoviesLocalDataSource
import com.elna.moviedb.core.database.getMovieDao
import com.elna.moviedb.core.database.getMovieDetailsDao
import com.elna.moviedb.core.database.getRoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

val databaseModule = module {
    includes(platformDatabaseBuilder())

    single {
        getRoomDatabase(
            builder = get(),
            appDispatchers = get()
        )
    }
    single { getMovieDao(get()) }
    single { getMovieDetailsDao(get()) }

    single {
        MoviesLocalDataSource(
            movieDao = get(),
            movieDetailsDao = get(),
        )
    }
}

internal expect fun platformDatabaseBuilder(): Module
