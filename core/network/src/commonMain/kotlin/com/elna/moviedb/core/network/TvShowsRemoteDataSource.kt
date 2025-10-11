package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.model.tv_shows.RemoteTvShowDetails
import com.elna.moviedb.core.network.model.tv_shows.RemoteTvShowsPage
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

    suspend fun getTvShowDetails(tvShowId: Int, language: String): RemoteTvShowDetails {
        return withContext(appDispatchers.io) {
            httpClient.get("${TMDB_BASE_URL}tv/${tvShowId}") {
                url {
                    parameters.append("api_key", TMDB_API_KEY)
                    parameters.append("language", language)
                }
            }.body<RemoteTvShowDetails>()
        }
    }

    private suspend fun fetchTvShowsPage(
        path: String,
        page: Int,
        language: String
    ): AppResult<RemoteTvShowsPage> {
        return try {
            val tvShowsPage = withContext(appDispatchers.io) {
                httpClient.get("${TMDB_BASE_URL}$path") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                    }
                }.body<RemoteTvShowsPage>()
            }
            AppResult.Success(tvShowsPage)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Unknown error occurred")
        }
    }
}
