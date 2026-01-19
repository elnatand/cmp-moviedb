package com.elna.moviedb.core.network.model.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteSearchPerson(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("profile_path")
    val profilePath: String?,
    @SerialName("known_for_department")
    val knownForDepartment: String?,
    @SerialName("popularity")
    val popularity: Double?,
    @SerialName("adult")
    val adult: Boolean?
)
