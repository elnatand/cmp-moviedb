package com.elna.moviedb.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.movies
import com.elna.moviedb.resources.profile
import com.elna.moviedb.resources.search
import com.elna.moviedb.resources.tv_shows
import org.jetbrains.compose.resources.StringResource

enum class TopLevelDestination(
    val icon: ImageVector,
    val titleRes: StringResource,
    val route: Route,
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        titleRes = Res.string.movies,
        route = MoviesRoute.MoviesListRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        titleRes = Res.string.tv_shows,
        route = TvShowsRoute.TvShowsListRoute
    ),
    SEARCH(
        icon = Icons.Filled.Search,
        titleRes = Res.string.search,
        route = SearchRoute
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        titleRes = Res.string.profile,
        route = ProfileRoute
    ),
}
