package features.tv_shows.data

import features.tv_shows.model.TvShow
import features.tv_shows.model.TvShowDetails

interface TvShowsRepository {
    suspend fun getTvShowsPage(page: Int): List<TvShow>
    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails
}