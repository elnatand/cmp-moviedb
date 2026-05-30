package com.elna.moviedb.feature.tvshows

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.dto.credits.RemoteCredits
import com.elna.moviedb.core.network.dto.videos.RemoteVideoResponse
import com.elna.moviedb.feature.tvshows.data.datasources.TvShowsRemoteService
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShow
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShowDetails
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShowsPage

/**
 * Configurable fake for [TvShowsRemoteService].
 *
 * Page results can be set per TMDB path ([resultByPath]) or per page number ([pageByNumber]),
 * with [defaultPage] as the fallback. Records every fetched (path, page) pair so tests can
 * assert how many network calls happened.
 */
class FakeTvShowsRemoteService : TvShowsRemoteService {

    val resultByPath = mutableMapOf<String, AppResult<RemoteTvShowsPage>>()
    val pageByNumber = mutableMapOf<Int, AppResult<RemoteTvShowsPage>>()
    var defaultPage: AppResult<RemoteTvShowsPage> =
        AppResult.Success(RemoteTvShowsPage(page = 1, totalPages = 1, results = emptyList()))

    val fetchedPages = mutableListOf<Pair<String, Int>>()

    override suspend fun fetchTvShowsPage(
        apiPath: String,
        page: Int,
        language: String
    ): AppResult<RemoteTvShowsPage> {
        fetchedPages += apiPath to page
        return resultByPath[apiPath] ?: pageByNumber[page] ?: defaultPage
    }

    override suspend fun getTvShowDetails(tvShowId: Int, language: String): AppResult<RemoteTvShowDetails> =
        AppResult.Error("not used in these tests")

    override suspend fun getTvShowVideos(tvShowId: Int, language: String): AppResult<RemoteVideoResponse> =
        AppResult.Error("not used in these tests")

    override suspend fun getTvShowCredits(tvShowId: Int, language: String): AppResult<RemoteCredits> =
        AppResult.Error("not used in these tests")

    companion object {
        fun tvShow(id: Int, name: String) = RemoteTvShow(id = id, name = name, posterPath = null)
    }
}
