package com.example.moviedb.core.database.di

import com.example.moviedb.core.common.DISPATCHER_IO
import com.example.moviedb.core.database.getMovieDao
import com.example.moviedb.core.database.getMovieDetailsDao
import com.example.moviedb.core.database.getRoomDatabase
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
}
