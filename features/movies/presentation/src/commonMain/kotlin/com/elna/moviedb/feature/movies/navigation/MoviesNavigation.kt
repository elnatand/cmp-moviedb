package com.elna.moviedb.feature.movies.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.Navigator
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen


fun EntryProviderScope<Route>.moviesFlow(
    navigator: Navigator,
    sharedTransitionScope: SharedTransitionScope?
) {

    entry<MoviesRoute.MoviesListRoute> {
        MoviesScreen(
            onClick = { movieId, title, category ->
                navigator.navigate(MoviesRoute.MovieDetailsRoute(movieId, category.name), title)
            },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = if (sharedTransitionScope != null) LocalNavAnimatedContentScope.current else null
        )
    }

    entry<MoviesRoute.MovieDetailsRoute> {
        MovieDetailsScreen(
            movieId = it.movieId,
            category = it.category,
            onBack = navigator::goBack,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = if (sharedTransitionScope != null) LocalNavAnimatedContentScope.current else null,
            onCastMemberClick = { personId ->
                navigator.navigate(PersonDetailsRoute(personId))
            }
        )
    }
}
