package com.elna.moviedb.feature.person.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.person.ui.PersonDetailsScreen

fun EntryProviderScope<Route>.personDetailsEntry(
    rootBackStack: SnapshotStateList<Route>
) {
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
