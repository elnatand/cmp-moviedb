import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.mp.KoinPlatform.getKoin

actual class DatabaseDriverFactory {

    actual fun createDriver(): SqlDriver {

        return AndroidSqliteDriver(
            schema = schema,
            context = getKoin().get(),
            name = DB_NAME
        )
    }
}