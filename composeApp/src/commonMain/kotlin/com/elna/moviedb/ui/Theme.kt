import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import com.elna.moviedb.core.model.AppTheme
import com.elna.moviedb.core.ui.theme.AppTheme

@Composable
fun Theme(
    selectedTheme: String,
    onThemeChange: ((isDarkTheme: Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {

    val currentTheme = AppTheme.getAppThemeByValue(selectedTheme)
    val darkTheme = when (currentTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    // Lets a native host (the iOS 26 Liquid Glass shell) mirror the resolved theme in its
    // own chrome (tab bar, toolbar) — Compose can't restyle those.
    if (onThemeChange != null) {
        val currentOnThemeChange by rememberUpdatedState(onThemeChange)
        LaunchedEffect(darkTheme) {
            currentOnThemeChange(darkTheme)
        }
    }

    AppTheme(darkTheme = darkTheme) {
        content()
    }
}
