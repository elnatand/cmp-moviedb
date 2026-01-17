package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.model.tv_shows.RemoteTvShowCredits
import com.elna.moviedb.core.network.model.tv_shows.RemoteTvShowDetails
import com.elna.moviedb.core.network.model.tv_shows.RemoteTvShowsPage
import com.elna.moviedb.core.network.model.videos.RemoteVideoResponse
import com.elna.moviedb.core.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

class TvShowsRemoteService(
    private val httpClient: HttpClient,
    private val appDispatchers: AppDispatchers
) {

    /**
     * Fetches a page of TV shows for any category from TMDB API.
     *
     * This generic method supports all TV show categories.
     * New categories can be added without modifying this method.
     *
     * @param apiPath The TMDB API path (e.g., "tv/popular", "tv/on_the_air")
     * @param page The page number to fetch
     * @param language The language code for the results
     * @return AppResult containing the TV shows page or an error
     */
    suspend fun fetchTvShowsPage(
        apiPath: String,
        page: Int,
        language: String
    ): AppResult<RemoteTvShowsPage> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}$apiPath") {
                    url {
                        parameters.append("api_key", BuildKonfig.TMDB_API_KEY)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                    }
                }.body<RemoteTvShowsPage>()
            }
        }
    }

    suspend fun getTvShowDetails(tvShowId: Int, language: String): AppResult<RemoteTvShowDetails> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}/tv/${tvShowId}") {
                    url {
                        parameters.append("api_key", BuildKonfig.TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemoteTvShowDetails>()
            }
        }
    }

    suspend fun getTvShowVideos(tvShowId: Int, language: String): AppResult<RemoteVideoResponse> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}/tv/${tvShowId}/videos") {
                    url {
                        parameters.append("api_key", BuildKonfig.TMDB_API_KEY)
                        parameters.append("language", language)
                        parameters.append("include_video_language", "$language,null")
                    }
                }.body<RemoteVideoResponse>()
            }
        }
    }

    suspend fun getTvShowCredits(tvShowId: Int, language: String): AppResult<RemoteTvShowCredits> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}/tv/${tvShowId}/credits") {
                    url {
                        parameters.append("api_key", BuildKonfig.TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemoteTvShowCredits>()
            }
        }
    }
}
