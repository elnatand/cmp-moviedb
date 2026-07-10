package com.elna.moviedb.feature.person.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.feature.person.domain.model.MediaType
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.Navigator
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.person.presentation.ui.PersonDetailsScreen

fun EntryProviderScope<Route>.personDetailsEntry(
    navigator: Navigator
) {
    entry<PersonDetailsRoute> {
        PersonDetailsScreen(
            personId = it.personId,
            onBack = navigator::goBack,
            onCreditClick = { id, mediaType ->
                when (mediaType) {
                    MediaType.MOVIE -> navigator.navigate(MoviesRoute.MovieDetailsRoute(id))
                    MediaType.TV -> navigator.navigate(TvShowsRoute.TvShowDetailsRoute(id))
                }
            }
        )
    }
}
