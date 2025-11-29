package com.elna.moviedb.feature.person.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.feature.person.ui.PersonDetailsScreen

fun personDetailsEntry(
    key: PersonDetailsRoute,
    backStack: NavBackStack<NavKey>
): NavEntry<NavKey> {
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
