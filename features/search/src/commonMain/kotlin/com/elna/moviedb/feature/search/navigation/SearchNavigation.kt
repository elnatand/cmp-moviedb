package com.elna.moviedb.feature.search.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.search.ui.SearchScreen

fun EntryProviderScope<Route>.searchEntry(
    rootBackStack: SnapshotStateList<Route>
) {
    entry<SearchRoute> {
        SearchScreen(
            onMovieClicked = { movieId ->
                rootBackStack.add(
                    MoviesRoute(startAt = MoviesRoute.MovieDetailsRoute(movieId))
                )
            },
            onTvShowClicked = { tvShowId ->
                rootBackStack.add(
                    TvShowsRoute(startAt = TvShowsRoute.TvShowDetailsRoute(tvShowId))
                )
            },
            onPersonClicked = { personId ->
                rootBackStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}
