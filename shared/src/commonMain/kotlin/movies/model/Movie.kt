package movies.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: Int,
    val poster_path: String?,
)