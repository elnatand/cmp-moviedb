package com.elna.moviedb.feature.person.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TVShowsRoute
import com.elna.moviedb.feature.person.ui.PersonDetailsScreen

fun EntryProviderScope<Route>.personDetailsEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<PersonDetailsRoute> {
        PersonDetailsScreen(
            personId = it.personId,
            onCreditClick = { id, mediaType ->
                when (mediaType) {
                    MediaType.MOVIE -> backStack.add(MoviesRoute.MovieDetailsRoute(id))
                    MediaType.TV -> backStack.add(TVShowsRoute.TvShowDetailsRoute(id))
                }
            }
        )
    }
}
