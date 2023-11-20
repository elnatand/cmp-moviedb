package com.example.moviedb.ui.navigation

import androidx.compose.runtime.Composable
import com.example.moviedb.features.movies.navigation.movieDetailsScene
import com.example.moviedb.features.movies.navigation.moviesRoute
import com.example.moviedb.features.movies.navigation.moviesScene
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import com.example.moviedb.features.profile.navigation.profileScene
import com.example.moviedb.features.tv_shows.navigation.tvShowDetailsScene
import com.example.moviedb.features.tv_shows.navigation.tvShowsScene
import moe.tlaster.precompose.navigation.NavOptions


@Composable
fun RootNavGraph(navigator: Navigator = rememberNavigator()) {
    val navOptions = NavOptions(
        // Launch the scene as single top
        launchSingleTop = true,
    )
    NavHost(
        navigator = navigator,
        initialRoute = moviesRoute,
    ) {
        moviesScene(navigator, navOptions)
        movieDetailsScene(navigator)
        tvShowsScene(navigator, navOptions)
        tvShowDetailsScene(navigator)
        profileScene()
    }
}
