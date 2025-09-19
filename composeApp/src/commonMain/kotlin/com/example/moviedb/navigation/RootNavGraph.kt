package com.example.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.moviedb.feature.movies.navigation.MOVIES
import com.example.moviedb.feature.tvshows.navigation.tvShowsScene
import com.example.moviedb.feature.movies.navigation.moviesScene
import com.example.moviedb.feature.profile.navigation.profileScene


@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: Any = MOVIES,
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        moviesScene(navController)
//        movieDetailsScene(navigator)
        tvShowsScene(navController)
//        tvShowDetailsScene(navigator)
        profileScene()
    }
}
