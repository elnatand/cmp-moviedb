package com.elna.moviedb.feature.movies.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen

fun EntryProviderScope<Route>.moviesEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<MoviesRoute> {
        MoviesScreen(onClick = { movieId, _ ->
            backStack.add(MovieDetailsRoute(movieId))
        })
    }
}

fun EntryProviderScope<Route>.movieDetailsEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<MovieDetailsRoute> {
        MovieDetailsScreen(
            movieId = it.movieId,
            onCastMemberClick = { personId ->
                backStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}
