package com.elna.moviedb.core.network.model.tv_shows

import com.elna.moviedb.core.model.CastMember
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteTvShowCredits(
    @SerialName("cast")
    val cast: List<RemoteCastMember>?,
    @SerialName("id")
    val id: Int?
)

@Serializable
data class RemoteCastMember(
    @SerialName("adult")
    val adult: Boolean?,
    @SerialName("gender")
    val gender: Int?,
    @SerialName("id")
    val id: Int,
    @SerialName("known_for_department")
    val knownForDepartment: String?,
    @SerialName("name")
    val name: String,
    @SerialName("original_name")
    val originalName: String?,
    @SerialName("popularity")
    val popularity: Double?,
    @SerialName("profile_path")
    val profilePath: String?,
    @SerialName("character")
    val character: String,
    @SerialName("credit_id")
    val creditId: String?,
    @SerialName("order")
    val order: Int
)

fun RemoteCastMember.toDomain() = CastMember(
    id = id,
    name = name,
    character = character,
    profilePath = profilePath,
    order = order
)
