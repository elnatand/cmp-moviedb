package com.elna.moviedb.feature.movies.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen

fun moviesEntry(
    key: Any,
    backStack: SnapshotStateList<Any>
): NavEntry<Any> {
    return NavEntry(key = key) {
        MoviesScreen(onClick = { movieId, _ ->
            backStack.add(MovieDetailsRoute(movieId))
        })
    }
}

fun movieDetailsEntry(
    key: MovieDetailsRoute,
    backStack: SnapshotStateList<Any>
): NavEntry<Any> {
    return NavEntry(key = key) {
        MovieDetailsScreen(
            movieId = key.movieId,
            onCastMemberClick = { personId ->
                backStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}
