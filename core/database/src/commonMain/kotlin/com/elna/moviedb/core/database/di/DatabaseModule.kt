package com.elna.moviedb.core.database.di

import com.elna.moviedb.core.common.DISPATCHER_IO
import com.elna.moviedb.core.database.MoviesLocalDataSource
import com.elna.moviedb.core.database.getMovieDao
import com.elna.moviedb.core.database.getMovieDetailsDao
import com.elna.moviedb.core.database.getRoomDatabase
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module


expect fun platformDatabaseModule(): Module

val databaseModule = module {
    single {
        getRoomDatabase(
            builder = get(),
            appDispatcher = get(named(DISPATCHER_IO))
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
