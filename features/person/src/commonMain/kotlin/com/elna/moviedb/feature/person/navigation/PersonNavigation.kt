package com.elna.moviedb.feature.person.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.person.ui.PersonDetailsScreen

fun EntryProviderScope<Route>.personDetailsEntry(
    onCreditClick: (id: Int, mediaType: MediaType) -> Unit
) {
    entry<PersonDetailsRoute> {
        PersonDetailsScreen(
            personId = it.personId,
            onCreditClick = onCreditClick
        )
    }
}
