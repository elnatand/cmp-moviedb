package com.elna.moviedb.feature.movies.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen


fun EntryProviderScope<Route>.moviesFlow(
    rootBackStack: SnapshotStateList<Route>,
    sharedTransitionScope: SharedTransitionScope
) {

    entry<MoviesRoute.MoviesListRoute> {
        MoviesScreen(
            onClick = { movieId, _, category ->
                rootBackStack.add(MoviesRoute.MovieDetailsRoute(movieId, category.name))
            },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = LocalNavAnimatedContentScope.current
        )
    }

    entry<MoviesRoute.MovieDetailsRoute> {
        MovieDetailsScreen(
            movieId = it.movieId,
            category = it.category,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
            onCastMemberClick = { personId ->
                rootBackStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}
