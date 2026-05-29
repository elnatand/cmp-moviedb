package com.elna.moviedb.feature.search.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteMultiSearchPage(
    @SerialName("page")
    val page: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int,
    @SerialName("results")
    val results: List<RemoteMultiSearchItem>
)