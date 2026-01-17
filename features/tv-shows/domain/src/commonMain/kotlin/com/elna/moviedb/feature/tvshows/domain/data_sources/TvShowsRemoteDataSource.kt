package com.elna.moviedb.feature.tvshows.domain.data_sources

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.tv_shows.RemoteTvShowDetails
import com.elna.moviedb.core.network.model.tv_shows.RemoteTvShowsPage

interface TvShowsRemoteDataSource {
    suspend fun fetchTvShowsPage(
        apiPath: String,
        page: Int,
    ): AppResult<RemoteTvShowsPage>
}