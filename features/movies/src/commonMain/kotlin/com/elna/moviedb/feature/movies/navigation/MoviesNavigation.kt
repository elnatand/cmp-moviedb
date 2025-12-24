package com.elna.moviedb.feature.movies.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen


fun EntryProviderScope<Route>.moviesFlow(
    rootBackStack: SnapshotStateList<Route>
) {

    entry<MoviesRoute.MoviesListRoute> {
        MoviesScreen(onClick = { movieId, _ ->
            rootBackStack.add(MoviesRoute.MovieDetailsRoute(movieId))
        })
    }

    entry<MoviesRoute.MovieDetailsRoute> {
        MovieDetailsScreen(
            movieId = it.movieId,
            onCastMemberClick = { personId ->
                rootBackStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}
