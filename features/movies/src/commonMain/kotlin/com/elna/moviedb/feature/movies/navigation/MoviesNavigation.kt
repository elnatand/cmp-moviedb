package com.elna.moviedb.feature.movies.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen

fun EntryProviderScope<Route>.moviesEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<MoviesRoute.MoviesListRoute> {
        MoviesScreen(onClick = { movieId, _ ->
            backStack.add(MoviesRoute.MovieDetailsRoute(movieId))
        })
    }
}

fun EntryProviderScope<Route>.movieDetailsEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<MoviesRoute.MovieDetailsRoute> {
        MovieDetailsScreen(
            movieId = it.movieId,
            onCastMemberClick = { personId ->
                backStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}
