package com.elna.moviedb.core.network.dto.credits

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.CastMember
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Shared credits DTO for the TMDB `/credits` endpoint, used by multiple features
 * (movies, TV shows). The response shape is identical across media types.
 */
@Serializable
data class RemoteCredits(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("cast")
    val cast: List<RemoteCastMember>? = null
)

@Serializable
data class RemoteCastMember(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("gender")
    val gender: Int? = null,
    @SerialName("id")
    val id: Int,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,
    @SerialName("name")
    val name: String,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("profile_path")
    val profilePath: String? = null,
    @SerialName("character")
    val character: String,
    @SerialName("credit_id")
    val creditId: String? = null,
    @SerialName("order")
    val order: Int
)

fun RemoteCastMember.toDomain(): CastMember = CastMember(
    id = id,
    name = name,
    character = character,
    profilePath = profilePath,
    order = order
)

/**
 * Maps a credits result to a domain cast list, sorted by billing [order].
 *
 * Cast is optional detail data: on error this degrades gracefully to an empty list
 * rather than failing the whole details fetch.
 */
fun AppResult<RemoteCredits>.toCastMembersOrEmpty(): List<CastMember> = when (this) {
    is AppResult.Success -> data.cast
        ?.sortedBy { it.order }
        ?.map { it.toDomain() }
        ?: emptyList()

    is AppResult.Error -> emptyList()
}
