package com.elna.moviedb.feature.person.api.navigation

import com.elna.moviedb.core.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class PersonDetailsRoute(
    val personId: Int,
) : Route
