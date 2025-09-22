package com.example.moviedb.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.moviedb.core.database.model.MovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO


@Database(entities = [MovieEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMovieDao(): MovieDao
}

// The Room compiler generates the `actual` implementations.
//@Suppress("KotlinNoActualForExpect")
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun getMovieDao(database: AppDatabase) = database.getMovieDao()


