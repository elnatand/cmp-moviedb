package data.tv_shows

import model.TvShow
import model.TvShowDetails

interface TvShowsRepository {
    suspend fun getTvShowsPage(page: Int): List<TvShow>
    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails
}