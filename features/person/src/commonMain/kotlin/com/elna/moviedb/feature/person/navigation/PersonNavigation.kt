package com.elna.moviedb.feature.person.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.feature.person.ui.PersonDetailsScreen

fun personDetailsEntry(
    key: PersonDetailsRoute,
    backStack: SnapshotStateList<Any>
): NavEntry<Any> {
    return NavEntry(key = key) {
        PersonDetailsScreen(
            personId = key.personId,
            onCreditClick = { id, mediaType ->
                when (mediaType) {
                    MediaType.MOVIE -> backStack.add(MovieDetailsRoute(id))
                    MediaType.TV -> backStack.add(TvShowDetailsRoute(id))
                }
            }
        )
    }
}
