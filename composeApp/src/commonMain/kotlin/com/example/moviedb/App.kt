package com.example.moviedb

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.moviedb.navigation.RootNavGraph
import com.example.moviedb.ui.LayoutDirectionWrapper
import com.example.moviedb.ui.NavigationBar
import moe.tlaster.precompose.PreComposeApp
import org.koin.compose.KoinContext

@Composable
fun App() {

    PreComposeApp {
        val appState: AppState = rememberAppState()
        KoinContext {
            MaterialTheme {
                LayoutDirectionWrapper {
                    Scaffold(
                        bottomBar = {
                            if (appState.shouldShowBottomBar()) {
                                NavigationBar(
                                    topLevelDestinations = appState.topLevelDestinations,
                                    currentDestination = appState.currentDestination,
                                    onClick = appState::navigateToTopLevelDestination
                                )
                            }
                        }
                    ) {
                        RootNavGraph()
                    }
                }
            }
        }
    }
}