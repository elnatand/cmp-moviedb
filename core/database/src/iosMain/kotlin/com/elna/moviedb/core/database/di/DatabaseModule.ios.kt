package com.elna.moviedb.core.database.di

import androidx.room.RoomDatabase
import com.elna.moviedb.core.database.AppDatabase
import getDatabaseBuilder
import org.koin.dsl.module

actual fun platformDatabaseModule() = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }
}