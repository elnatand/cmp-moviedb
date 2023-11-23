package com.example.moviedb.navigation

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import com.example.moviedb.profile.navigation.profileScene
import com.example.moviedb.tvshows.navigation.tvShowDetailsScene
import com.example.moviedb.tvshows.navigation.tvShowsScene
import com.example.moviedb.movies.navigation.movieDetailsScene
import com.example.moviedb.movies.navigation.moviesRoute
import com.example.moviedb.movies.navigation.moviesScene
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
