package com.elna.moviedb

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.core.datastore.AppSettingsPreferences
import com.elna.moviedb.core.designsystem.theme.AppTheme
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.ThemeConfig
import com.elna.moviedb.navigation.RootNavGraph
import com.elna.moviedb.ui.Localization
import com.elna.moviedb.ui.NavigationBar
import org.koin.compose.koinInject

@Composable
fun App() {
    val appState: AppState = rememberAppState()
    val preferencesManager: AppSettingsPreferences = koinInject()

    val selectedLanguage by preferencesManager.getAppLanguageCode()
        .collectAsStateWithLifecycle(AppLanguage.ENGLISH.code)

    val selectedTheme by preferencesManager.getThemeConfig()
        .collectAsStateWithLifecycle(ThemeConfig.SYSTEM.value)

    Localization(selectedLanguage) {
        AppTheme(selectedTheme) {
            Scaffold(
                bottomBar = {
                    NavigationBar(appState = appState)
                }
            ) {
                RootNavGraph(appState.navBackStack)
            }
        }
    }
}
