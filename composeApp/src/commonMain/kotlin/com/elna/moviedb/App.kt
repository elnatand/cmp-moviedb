package com.elna.moviedb

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.elna.moviedb.navigation.RootNavGraph
import com.elna.moviedb.ui.Localization
import com.elna.moviedb.ui.NavigationBar

@Composable
fun App() {
    val appState: AppState = rememberAppState()

    Localization {
        MaterialTheme {
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
