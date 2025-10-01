package com.elna.moviedb.core.network.model.search

import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL
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

fun RemoteSearchPerson.toSearchResult(): SearchResultItem.PersonItem {
    return SearchResultItem.PersonItem(
        id = id,
        name = name,
        knownForDepartment = knownForDepartment,
        profilePath = profilePath?.let { "$TMDB_IMAGE_URL$it" }
    )
}
