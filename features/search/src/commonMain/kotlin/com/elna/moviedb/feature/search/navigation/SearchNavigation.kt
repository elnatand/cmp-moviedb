package com.elna.moviedb.feature.search.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.feature.search.ui.SearchScreen

fun EntryProviderScope<Route>.searchEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<SearchRoute> {
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