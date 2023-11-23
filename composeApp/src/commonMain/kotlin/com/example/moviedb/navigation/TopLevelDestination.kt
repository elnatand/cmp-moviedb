package com.example.moviedb.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.moviedb.profile.navigation.profileRoute
import com.example.moviedb.tvshows.navigation.tvShowsRoute
import com.example.moviedb.ui.MR
import com.example.moviedb.movies.navigation.moviesRoute
import dev.icerock.moko.resources.StringResource


enum class TopLevelDestination(
    val icon: ImageVector,
    val titleRes: StringResource,
    val route: String
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        titleRes = MR.strings.movies,
        route = moviesRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        titleRes = MR.strings.tv_shows,
        route = tvShowsRoute
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        titleRes = MR.strings.profile,
        route = profileRoute
    ),
}