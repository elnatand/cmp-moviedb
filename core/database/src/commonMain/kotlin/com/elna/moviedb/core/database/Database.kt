package com.elna.moviedb.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.database.model.CastMemberEntity
import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.MovieEntity
import com.elna.moviedb.core.database.model.VideoEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.prepare("ALTER TABLE MovieDetailsEntity ADD COLUMN directors TEXT DEFAULT NULL")
            .use { it.step() }
    }
}

@Database(
    entities = [
        MovieEntity::class,
        MovieDetailsEntity::class,
        VideoEntity::class,
        CastMemberEntity::class
    ],
    version = 2
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMovieDao(): MovieDao
    abstract fun getMovieDetailsDao(): MovieDetailsDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
    appDispatchers: AppDispatchers
): AppDatabase {
    return builder
        .addMigrations(MIGRATION_1_2)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(appDispatchers.io)
        .build()
}

fun getMovieDao(database: AppDatabase) = database.getMovieDao()
fun getMovieDetailsDao(database: AppDatabase) = database.getMovieDetailsDao()


