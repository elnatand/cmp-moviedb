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
    suspend fun getPopularTvShowsPage(page: Int, language: String): AppResult<RemoteTvShowsPage> {
        return try {
            val tvShowsPage = withContext(appDispatchers.io) {
                httpClient.get("${TMDB_BASE_URL}tv/popular") {
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

    /**
     * Fetches a page of on-the-air TV shows from TMDB API.
     * On-the-air shows are TV series that are currently airing new episodes.
     *
     * @param page Page number to fetch (1-indexed)
     * @param language Language code for localized content (e.g., "en-US", "ar-SA")
     * @return AppResult containing RemoteTvShowsPage on success, error message on failure
     */
    suspend fun getOnTheAirTvShowsPage(page: Int, language: String): AppResult<RemoteTvShowsPage> {
        return try {
            val tvShowsPage = withContext(appDispatchers.io) {
                httpClient.get("${TMDB_BASE_URL}tv/on_the_air") {
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

    /**
     * Fetches a page of top-rated TV shows from TMDB API.
     * Top-rated shows are TV series with the highest average ratings.
     *
     * @param page Page number to fetch (1-indexed)
     * @param language Language code for localized content (e.g., "en-US", "ar-SA")
     * @return AppResult containing RemoteTvShowsPage on success, error message on failure
     */
    suspend fun getTopRatedTvShowsPage(page: Int, language: String): AppResult<RemoteTvShowsPage> {
        return try {
            val tvShowsPage = withContext(appDispatchers.io) {
                httpClient.get("${TMDB_BASE_URL}tv/top_rated") {
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
}
