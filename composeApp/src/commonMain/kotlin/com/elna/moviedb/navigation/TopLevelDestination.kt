package com.elna.moviedb.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.elna.moviedb.feature.movies.navigation.MoviesRoute
import com.elna.moviedb.feature.profile.navigation.ProfileRoute
import com.elna.moviedb.feature.search.navigation.SearchRoute
import com.elna.moviedb.feature.tvshows.navigation.TvShowsRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val icon: ImageVector,
    val route: KClass<*>,
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        route = MoviesRoute::class
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        route = TvShowsRoute::class
    ),
    SEARCH(
        icon = Icons.Filled.Search,
        route = SearchRoute::class
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        route = ProfileRoute::class
    ),
}