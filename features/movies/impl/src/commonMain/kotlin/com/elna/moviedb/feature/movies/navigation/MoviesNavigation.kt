package com.elna.moviedb.feature.movies.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.core.navigation.Route
import com.elna.moviedb.feature.movie.api.navigation.MoviesRoute
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen
import com.elna.moviedb.feature.person.api.navigation.PersonDetailsRoute


/**
 * A [NavigationFactory] that provides the navigation graph for the movies feature.
 *
 * This factory defines the composable destinations for the movies list and movie details screens.
 * It handles the navigation logic between these screens and to other features like the person details screen.
 */
class MoviesNavigationFactory : NavigationFactory {
    override fun create(
        builder: EntryProviderScope<Route>,
        rootBackStack: SnapshotStateList<Route>,
        sharedTransitionScope: SharedTransitionScope
    ) = with(builder) {
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
                onBack = { rootBackStack.removeLastOrNull() },
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                onCastMemberClick = { personId ->
                    rootBackStack.add(PersonDetailsRoute(personId))
                }
            )
        }
    }
}
