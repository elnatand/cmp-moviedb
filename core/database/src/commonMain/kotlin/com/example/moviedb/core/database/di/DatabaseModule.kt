package com.example.moviedb.core.database.di
import DatabaseDriverFactory
import com.example.moviedb.core.database.Database
import org.koin.dsl.module

val databaseModule = module {
    single {
        Database(
            databaseDriverFactory = DatabaseDriverFactory()
        )
    }
}
