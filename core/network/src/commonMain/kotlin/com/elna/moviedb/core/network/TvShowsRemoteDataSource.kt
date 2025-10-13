package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
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

class TvShowsRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatchers: AppDispatchers
) {

    suspend fun getPopularTvShowsPage(page: Int, language: String) =
        fetchTvShowsPage("tv/popular", page, language)

    suspend fun getOnTheAirTvShowsPage(page: Int, language: String) =
        fetchTvShowsPage("tv/on_the_air", page, language)

    suspend fun getTopRatedTvShowsPage(page: Int, language: String) =
        fetchTvShowsPage("tv/top_rated", page, language)

    suspend fun getTvShowDetails(tvShowId: Int, language: String): AppResult<RemoteTvShowDetails> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}tv/${tvShowId}") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemoteTvShowDetails>()
            }
        }
    }

    suspend fun getTvShowVideos(tvShowId: Int, language: String): AppResult<RemoteVideoResponse> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}tv/${tvShowId}/videos") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemoteVideoResponse>()
            }
        }
    }

    suspend fun getTvShowCredits(tvShowId: Int, language: String): AppResult<RemoteTvShowCredits> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}tv/${tvShowId}/credits") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemoteTvShowCredits>()
            }
        }
    }

    private suspend fun fetchTvShowsPage(
        path: String,
        page: Int,
        language: String
    ): AppResult<RemoteTvShowsPage> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}$path") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                    }
                }.body<RemoteTvShowsPage>()
            }
        }
    }
}
