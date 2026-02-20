package com.elna.moviedb.feature.person.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.core.navigation.Route
import com.elna.moviedb.feature.movie.api.navigation.MoviesRoute
import com.elna.moviedb.feature.person.api.navigation.PersonDetailsRoute
import com.elna.moviedb.feature.person.ui.PersonDetailsScreen
import com.elna.moviedb.feature.tvshows.api.navigation.TvShowsRoute

/**
 * A [NavigationFactory] responsible for creating the navigation entry for the person details screen.
 * This factory defines how to navigate to and display the [PersonDetailsScreen].
 * It handles the creation of the screen, passing the necessary person ID, and defining
 * actions for navigating back or to other details screens (like movie or TV show details)
 * when a user interacts with the UI.
 */
class PersonDetailsNavigationFactory : NavigationFactory {
    override fun create(
        builder: EntryProviderScope<Route>,
        rootBackStack: SnapshotStateList<Route>,
        sharedTransitionScope: SharedTransitionScope
    ) {
        with(builder) {
            entry<PersonDetailsRoute> {
                PersonDetailsScreen(
                    personId = it.personId,
                    onBack = { rootBackStack.removeLastOrNull() },
                    onCreditClick = { id, mediaType ->
                        when (mediaType) {
                            MediaType.MOVIE -> rootBackStack.add(MoviesRoute.MovieDetailsRoute(id))
                            MediaType.TV -> rootBackStack.add(TvShowsRoute.TvShowDetailsRoute(id))
                        }
                    }
                )
            }
        }
    }
}
