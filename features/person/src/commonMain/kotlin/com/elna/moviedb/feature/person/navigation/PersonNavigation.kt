package com.elna.moviedb.feature.person.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.feature.person.ui.PersonDetailsScreen

fun EntryProviderScope<Route>.personDetailsEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<PersonDetailsRoute> {
        PersonDetailsScreen(
            personId = it.personId,
            onCreditClick = { id, mediaType ->
                when (mediaType) {
                    MediaType.MOVIE -> backStack.add(MovieDetailsRoute(id))
                    MediaType.TV -> backStack.add(TvShowDetailsRoute(id))
                }
            }
        )
    }
}
