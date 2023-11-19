package data.tv_shows

import data.tv_shows.data_sources.TvShowsRemoteDataSource
import model.TvShow
import model.TvShowDetails

class TvShowRepositoryImpl(
    private val tvShowsRemoteDataSource: TvShowsRemoteDataSource
) : TvShowsRepository {

    override suspend fun getTvShowsPage(page: Int): List<TvShow> {
        return tvShowsRemoteDataSource.getTvShowPage(page)
    }

    override suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails {
        return tvShowsRemoteDataSource.getTvShowDetails(tvShowId)
    }
}