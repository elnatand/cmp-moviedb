package com.elna.moviedb.feature.search.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.feature.search.ui.SearchScreen

fun searchEntry(
    key: SearchRoute,
    backStack: NavBackStack<NavKey>
): NavEntry<NavKey> {
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