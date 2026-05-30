package com.elna.moviedb.feature.tvshows.data.datasources

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.TmdbApiClient
import com.elna.moviedb.core.network.dto.credits.RemoteCredits
import com.elna.moviedb.core.network.dto.videos.RemoteVideoResponse
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShowDetails
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShowsPage

/**
 * Remote source for TV show data from the TMDB API.
 *
 * Declared as an interface (mirroring [com.elna.moviedb.feature.movies.datasources.MoviesRemoteDataSource])
 * so the repository depends on an abstraction and can be unit-tested with a fake, without
 * standing up a real HTTP client.
 */
interface TvShowsRemoteService {
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
    ): AppResult<RemoteTvShowsPage>

    suspend fun getTvShowDetails(tvShowId: Int, language: String): AppResult<RemoteTvShowDetails>

    suspend fun getTvShowVideos(tvShowId: Int, language: String): AppResult<RemoteVideoResponse>

    suspend fun getTvShowCredits(tvShowId: Int, language: String): AppResult<RemoteCredits>
}

class TvShowsRemoteServiceImpl(
    private val apiClient: TmdbApiClient
) : TvShowsRemoteService {

    override suspend fun fetchTvShowsPage(
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

    override suspend fun getTvShowDetails(tvShowId: Int, language: String): AppResult<RemoteTvShowDetails> {
        return apiClient.get(
            path = "/tv/$tvShowId",
            "language" to language
        )
    }

    override suspend fun getTvShowVideos(tvShowId: Int, language: String): AppResult<RemoteVideoResponse> {
        // include_video_language expects ISO-639-1 codes ("en"), not the regional form
        // ("en-US") used for `language`; passing the regional form filters out all videos.
        val languageCode = language.substringBefore("-")
        return apiClient.get(
            path = "/tv/$tvShowId/videos",
            "language" to language,
            "include_video_language" to "$languageCode,null"
        )
    }

    override suspend fun getTvShowCredits(tvShowId: Int, language: String): AppResult<RemoteCredits> {
        return apiClient.get(
            path = "/tv/$tvShowId/credits",
            "language" to language
        )
    }
}
