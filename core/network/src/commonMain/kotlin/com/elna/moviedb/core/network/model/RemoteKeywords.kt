package com.elna.moviedb.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteKeyword(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)

/** Response for /movie/{id}/keywords */
@Serializable
data class RemoteMovieKeywordsResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("keywords")
    val keywords: List<RemoteKeyword>
)

/** Response for /tv/{id}/keywords */
@Serializable
data class RemoteTvKeywordsResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("results")
    val results: List<RemoteKeyword>
)
