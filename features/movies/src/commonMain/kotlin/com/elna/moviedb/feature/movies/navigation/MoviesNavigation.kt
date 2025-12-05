package com.elna.moviedb.feature.movies.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen

fun moviesEntry(
    key: Route,
    backStack: SnapshotStateList<Route>,
    sharedTransitionScope: SharedTransitionScope
): NavEntry<Route> {
    return NavEntry(key = key) {
        val animatedContentScope = LocalNavAnimatedContentScope.current
        MoviesScreen(
            onClick = { movieId, _ ->
                backStack.add(MovieDetailsRoute(movieId))
            },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedContentScope
        )
    }
}

fun movieDetailsEntry(
    key: MovieDetailsRoute,
    backStack: SnapshotStateList<Route>,
    sharedTransitionScope: SharedTransitionScope
): NavEntry<Route> {
    return NavEntry(key = key) {
        val animatedContentScope = LocalNavAnimatedContentScope.current
        MovieDetailsScreen(
            movieId = key.movieId,
            onCastMemberClick = { personId ->
                backStack.add(PersonDetailsRoute(personId))
            },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedContentScope
        )
    }
}
