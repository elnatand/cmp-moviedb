package features.tv_shows.data

import features.tv_shows.data.data_sources.TvShowsRemoteDataSource
import features.tv_shows.model.TvShow
import features.tv_shows.model.TvShowDetails

class TvShowRepositoryImpl(
    private val tvShowsRemoteDataSource: TvShowsRemoteDataSource
) : TvShowsRepository {

    override suspend fun getTvShowsPage(): List<TvShow> {
        return tvShowsRemoteDataSource.getTvShowPage()
    }

    override suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails {
        return tvShowsRemoteDataSource.getTvShowDetails(tvShowId)
    }
}