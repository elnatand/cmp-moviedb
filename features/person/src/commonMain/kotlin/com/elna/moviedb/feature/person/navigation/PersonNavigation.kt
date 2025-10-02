package com.elna.moviedb.feature.person.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.feature.person.ui.PersonDetailsScreen

fun NavGraphBuilder.personDetailsScene(navigator: NavHostController) {
    composable<PersonDetailsRoute> { entry ->
        val params = entry.toRoute<PersonDetailsRoute>()
        val personId: Int = params.personId

        PersonDetailsScreen(
            personId = personId,
        )
    }
}