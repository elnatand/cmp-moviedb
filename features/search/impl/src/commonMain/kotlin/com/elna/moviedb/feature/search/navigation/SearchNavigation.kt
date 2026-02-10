package com.elna.moviedb.feature.search.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.core.navigation.Route
import com.elna.moviedb.feature.movie.api.navigation.MoviesRoute
import com.elna.moviedb.feature.person.api.navigation.PersonDetailsRoute
import com.elna.moviedb.feature.search.api.navigation.SearchRoute
import com.elna.moviedb.feature.search.ui.SearchScreen
import com.elna.moviedb.feature.tvshows.api.navigation.TvShowsRoute

/**
 * A [NavigationFactory] that defines the navigation graph for the search feature.
 * It handles the creation of the [SearchScreen] and defines the actions to be taken
 * when a search result (movie, TV show, or person) is clicked.
 */
class SearchNavigationFactory : NavigationFactory {
    override fun create(
        builder: EntryProviderScope<Route>,
        rootBackStack: SnapshotStateList<Route>,
        sharedTransitionScope: SharedTransitionScope
    ) = with(builder) {
        entry<SearchRoute> {
            SearchScreen(
                onMovieClicked = { movieId ->
                    rootBackStack.add(MoviesRoute.MovieDetailsRoute(movieId))
                },
                onTvShowClicked = { tvShowId ->
                    rootBackStack.add(TvShowsRoute.TvShowDetailsRoute(tvShowId))
                },
                onPersonClicked = { personId ->
                    rootBackStack.add(PersonDetailsRoute(personId))
                }
            )
        }
    }
}
