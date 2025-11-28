package com.elna.moviedb.feature.movies.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen

fun moviesEntry(
    key: NavKey,
    backStack: NavBackStack<NavKey>
): NavEntry<NavKey> {
    return NavEntry(key = key) {
        MoviesScreen(onClick = { movieId, _ ->
            backStack.add(MovieDetailsRoute(movieId))
        })
    }
}

fun movieDetailsEntry(
    key: MovieDetailsRoute,
    backStack: NavBackStack<NavKey>
): NavEntry<NavKey> {
    return NavEntry(key = key) {
        MovieDetailsScreen(
            movieId = key.movieId,
            onCastMemberClick = { personId ->
                backStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}