package com.example.moviedb

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.moviedb.navigation.RootNavGraph
import com.example.moviedb.ui.NavigationBar

@Composable
fun App() {
    val appState: AppState = rememberAppState()

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