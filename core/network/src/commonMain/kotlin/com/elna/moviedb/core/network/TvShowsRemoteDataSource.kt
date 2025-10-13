package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
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

    /**
         * Fetches a page of top-rated TV shows.
         *
         * @param page The page number to retrieve (1-based).
         * @param language The language code for localized results (e.g., "en-US").
         * @return `AppResult<RemoteTvShowsPage>` containing the requested page of top-rated TV shows on success, or an error on failure.
         */
        suspend fun getTopRatedTvShowsPage(page: Int, language: String) =
        fetchTvShowsPage("tv/top_rated", page, language)

    /**
     * Fetches detailed information for a TV show from the TMDB API.
     *
     * @param tvShowId The TMDB identifier of the TV show.
     * @param language The language code used to localize returned fields.
     * @return `AppResult<RemoteTvShowDetails>` containing the TV show details on success, or an error result otherwise.
     */
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

    /**
     * Fetches videos for a TV show from TMDB.
     *
     * @param tvShowId The TMDB TV show identifier.
     * @param language The language code for the results (e.g., "en-US").
     * @return An AppResult containing a RemoteVideoResponse on success, or an error state on failure.
     */
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

    /**
     * Fetches a single page of TV shows from the TMDB API for the specified endpoint, page number, and language.
     *
     * @param path The TMDB endpoint path suffix (for example "tv/popular" or "tv/top_rated").
     * @param page The page number to request (1-based).
     * @param language The language code for localized results (for example "en-US").
     * @return `AppResult<RemoteTvShowsPage>` containing the fetched TV shows page on success, or an error result otherwise.
     */
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