package com.example.moviedb.core.data.tv_shows

import com.example.moviedb.core.data.model.movies.toDomain
import com.example.moviedb.core.data.tv_shows.data_sources.TvShowsRemoteDataSource
import com.example.moviedb.core.model.TvShow
import com.example.moviedb.core.model.TvShowDetails

class TvShowRepositoryImpl(
    private val tvShowsRemoteDataSource: TvShowsRemoteDataSource
) : TvShowsRepository {

    override suspend fun getTvShowsPage(page: Int): List<TvShow> {
        return tvShowsRemoteDataSource.getTvShowPage(page).map {
            it.toDomain()
        }
    }

    override suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails {
        return tvShowsRemoteDataSource.getTvShowDetails(tvShowId).toDomain()
    }
}
