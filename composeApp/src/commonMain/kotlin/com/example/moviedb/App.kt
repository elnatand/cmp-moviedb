package com.example.moviedb

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.moviedb.navigation.RootNavGraph
import com.example.moviedb.ui.LayoutDirectionWrapper
import com.example.moviedb.ui.NavigationBar
import org.koin.compose.KoinContext

@Composable
fun App() {
    val appState: AppState = rememberAppState()
    KoinContext {
        MaterialTheme {
            LayoutDirectionWrapper {
                Scaffold(
                    bottomBar = {
                        if (appState.shouldShowBottomBar()) {
                            NavigationBar(appState = appState)
                        }
                    }
                ) {
                    RootNavGraph(appState.navController)
                }
            }
        }
    }
}