package com.elna.moviedb.feature.search.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.feature.search.ui.SearchScreen

fun searchEntry(
    key: Any,
    backStack: SnapshotStateList<Any>
): NavEntry<Any> {
    return NavEntry(key = key) {
        SearchScreen(
            onMovieClicked = { movieId ->
                backStack.add(MovieDetailsRoute(movieId))
            },
            onTvShowClicked = { tvShowId ->
                backStack.add(TvShowDetailsRoute(tvShowId))
            },
            onPersonClicked = { personId ->
                backStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}