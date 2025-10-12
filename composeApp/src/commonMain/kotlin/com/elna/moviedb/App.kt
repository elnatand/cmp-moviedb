package com.elna.moviedb

import Theme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import com.elna.moviedb.navigation.RootNavGraph
import com.elna.moviedb.ui.Localization
import com.elna.moviedb.ui.NavigationBar
import org.koin.compose.koinInject

@Composable
fun App() {
    val appState: AppState = rememberAppState()
    val preferencesManager: PreferencesManager = koinInject()

    val selectedLanguage by preferencesManager.getAppLanguageCode()
        .collectAsStateWithLifecycle(AppLanguage.ENGLISH.code)

    val selectedTheme by preferencesManager.getAppTheme()
        .collectAsStateWithLifecycle(AppTheme.SYSTEM.value)

    Localization(selectedLanguage) {
        Theme(selectedTheme) {
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
