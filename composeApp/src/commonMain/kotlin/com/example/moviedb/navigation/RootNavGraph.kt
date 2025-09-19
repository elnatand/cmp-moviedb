package com.example.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.moviedb.feature.movies.navigation.MOVIES
import com.example.moviedb.feature.profile.navigation.profileScene
import com.example.moviedb.feature.tvshows.navigation.tvShowDetailsScene
import com.example.moviedb.feature.tvshows.navigation.tvShowsScene
import com.example.moviedb.feature.movies.navigation.movieDetailsScene
import com.example.moviedb.feature.movies.navigation.moviesScene



@Composable
fun RootNavGraph(navigator: NavHostController = rememberNavController()) {
    NavHost(
        navController = navigator,
        startDestination = MOVIES,
    ) {
        moviesScene(navigator)
//        movieDetailsScene(navigator)
//        tvShowsScene(navigator, navOptions)
//        tvShowDetailsScene(navigator)
//        profileScene()
    }
}
