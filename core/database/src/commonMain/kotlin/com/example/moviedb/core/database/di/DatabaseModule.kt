package com.example.moviedb.core.database.di
import com.example.moviedb.core.database.getMovieDao
import com.example.moviedb.core.database.getRoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module


expect fun platformDatabaseModule(): Module

val databaseModule = module {
    single { getRoomDatabase(get()) }
    single { getMovieDao(get()) }
}
