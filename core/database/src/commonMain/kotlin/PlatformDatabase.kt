import app.cash.sqldelight.db.SqlDriver
import com.example.moviedb.core.database.MovieDbDatabase

const val DB_NAME = "MovieDbDatabase.db"
val schema = MovieDbDatabase.Schema

expect class DatabaseDriverFactory() {
    fun createDriver(): SqlDriver
}