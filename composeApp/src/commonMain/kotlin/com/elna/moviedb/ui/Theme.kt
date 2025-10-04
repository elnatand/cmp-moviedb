import androidx.compose.runtime.Composable
import com.elna.moviedb.core.model.AppTheme
import com.elna.moviedb.core.ui.theme.AppTheme
import com.elna.moviedb.core.ui.theme.isSystemInDarkTheme

@Composable
fun Theme(selectedTheme: String, content: @Composable () -> Unit) {

    val currentTheme = AppTheme.getAppThemeByValue(selectedTheme)
    val darkTheme = when (currentTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    AppTheme(darkTheme = darkTheme) {
        content()
    }
}