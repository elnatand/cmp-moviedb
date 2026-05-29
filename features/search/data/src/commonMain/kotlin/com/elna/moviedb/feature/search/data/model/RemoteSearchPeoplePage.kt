package com.elna.moviedb.feature.search.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteSearchPeoplePage(
    @SerialName("page")
    val page: Int,
    @SerialName("results")
    val results: List<RemoteSearchPerson>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)
