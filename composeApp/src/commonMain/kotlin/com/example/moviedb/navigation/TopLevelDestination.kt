package com.example.moviedb.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.moviedb.core.model.Strings
import com.example.moviedb.feature.profile.navigation.profileRoute
import com.example.moviedb.feature.tvshows.navigation.tvShowsRoute
import com.example.moviedb.feature.movies.navigation.moviesRoute


enum class TopLevelDestination(
    val icon: ImageVector,
    val titleRes: Strings,
    val route: String
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        titleRes = Strings.movies,
        route = moviesRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        titleRes = Strings.tv_shows,
        route = tvShowsRoute
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        titleRes = Strings.profile,
        route = profileRoute
    ),
}