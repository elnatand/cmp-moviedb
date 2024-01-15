import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.moviedb.core.database.MovieDbDatabase

actual class DatabaseDriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = MovieDbDatabase.Schema,
            name = "MovieDbDatabase.db"
        )
    }
}