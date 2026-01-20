package com.elna.moviedb.feature.tvshows.data.datasources

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.TmdbApiClient
import com.elna.moviedb.core.network.model.videos.RemoteVideoResponse
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShowCredits
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShowDetails
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShowsPage

class TvShowsRemoteService(
    private val apiClient: TmdbApiClient
) {

    /**
     * Fetches a page of TV shows for any category from TMDB API.
     *
     * This generic method supports all TV show categories.
     * New categories can be added without modifying this method.
     *
     * @param apiPath The TMDB API path (e.g., "/tv/popular", "/tv/on_the_air")
     * @param page The page number to fetch
     * @param language The language code for the results
     * @return AppResult containing the TV shows page or an error
     */
    suspend fun fetchTvShowsPage(
        apiPath: String,
        page: Int,
        language: String
    ): AppResult<RemoteTvShowsPage> {
        return apiClient.get(
            path = apiPath,
            "page" to page.toString(),
            "language" to language
        )
    }

    suspend fun getTvShowDetails(tvShowId: Int, language: String): AppResult<RemoteTvShowDetails> {
        return apiClient.get(
            path = "/tv/$tvShowId",
            "language" to language
        )
    }

    suspend fun getTvShowVideos(tvShowId: Int, language: String): AppResult<RemoteVideoResponse> {
        return apiClient.get(
            path = "/tv/$tvShowId/videos",
            "language" to language,
            "include_video_language" to "$language,null"
        )
    }

    suspend fun getTvShowCredits(tvShowId: Int, language: String): AppResult<RemoteTvShowCredits> {
        return apiClient.get(
            path = "/tv/$tvShowId/credits",
            "language" to language
        )
    }
}
