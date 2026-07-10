package com.elna.moviedb.feature.search.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.Navigator
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.search.presentation.ui.SearchScreen

fun EntryProviderScope<Route>.searchEntry(
    navigator: Navigator
) {
    entry<SearchRoute> {
        SearchScreen(
            onMovieClicked = { movieId ->
                navigator.navigate(MoviesRoute.MovieDetailsRoute(movieId))
            },
            onTvShowClicked = { tvShowId ->
                navigator.navigate(TvShowsRoute.TvShowDetailsRoute(tvShowId))
            },
            onPersonClicked = { personId ->
                navigator.navigate(PersonDetailsRoute(personId))
            }
        )
    }
}
