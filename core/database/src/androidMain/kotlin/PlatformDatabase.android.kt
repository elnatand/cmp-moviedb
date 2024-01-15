
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.moviedb.core.database.MovieDbDatabase
import org.koin.mp.KoinPlatform.getKoin

actual class DatabaseDriverFactory  {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = MovieDbDatabase.Schema,
            context = getKoin().get(),
            name = "MovieDbDatabase.db"
        )
    }
}