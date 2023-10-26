package features.tv_shows.model

import kotlinx.serialization.Serializable

@Serializable
data class TvShowDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String?
)