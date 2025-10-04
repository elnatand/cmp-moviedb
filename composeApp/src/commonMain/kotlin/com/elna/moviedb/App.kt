package com.elna.moviedb

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.model.AppTheme
import com.elna.moviedb.core.ui.theme.AppTheme
import com.elna.moviedb.core.ui.theme.isSystemInDarkTheme
import com.elna.moviedb.navigation.RootNavGraph
import com.elna.moviedb.ui.Localization
import com.elna.moviedb.ui.NavigationBar
import org.koin.compose.koinInject

@Composable
fun App() {
    val appState: AppState = rememberAppState()
    val preferencesManager: PreferencesManager = koinInject()
    val themeValue by preferencesManager.getAppTheme().collectAsState(AppTheme.SYSTEM.value)
    val currentTheme = AppTheme.getAppThemeByValue(themeValue)

    val darkTheme = when (currentTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    Localization {
        AppTheme(darkTheme = darkTheme) {
            Scaffold(
                bottomBar = {
                    NavigationBar(appState = appState)
                }
            ) {
                RootNavGraph(appState.navController)
            }
        }
    }
}
