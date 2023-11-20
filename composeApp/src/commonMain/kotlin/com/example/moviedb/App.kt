package com.example.moviedb

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import moe.tlaster.precompose.PreComposeApp
import org.koin.compose.KoinContext
import com.example.moviedb.ui.design_system.NavigationBar
import com.example.moviedb.ui.navigation.RootNavGraph

@Composable
fun App() {
    PreComposeApp {
      val appState: AppState = rememberAppState()
        KoinContext {
            MaterialTheme {
                Scaffold(
                    bottomBar = {
                        if (appState.shouldShowBottomBar()) {
                            NavigationBar(
                                topLevelDestinations = appState.topLevelDestinations,
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