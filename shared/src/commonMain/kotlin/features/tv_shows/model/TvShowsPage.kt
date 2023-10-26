package features.tv_shows.model

import features.tv_shows.model.TvShow
import kotlinx.serialization.Serializable

@Serializable
data class TvShowsPage(
    val page: Int,
    val total_pages: Int,
    val results: List<TvShow>
)