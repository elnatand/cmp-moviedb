package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.model.movies.RemoteMovieCredits
import com.elna.moviedb.core.network.model.movies.RemoteMovieDetails
import com.elna.moviedb.core.network.model.movies.RemoteMoviesPage
import com.elna.moviedb.core.network.model.videos.RemoteVideoResponse
import com.elna.moviedb.core.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext


class MoviesRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatchers: AppDispatchers
) {

    /**
     * Fetches a page of movies for any category from TMDB API.
     *
     * This generic method supports all movie categories.
     * New categories can be added without modifying this method.
     *
     * @param apiPath The TMDB API path (e.g., "movie/popular", "movie/top_rated")
     * @param page The page number to fetch
     * @param language The language code for the results
     * @return AppResult containing the movies page or an error
     */
    suspend fun fetchMoviesPage(
        apiPath: String,
        page: Int,
        language: String
    ): AppResult<RemoteMoviesPage> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}$apiPath") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                    }
                }.body<RemoteMoviesPage>()
            }
        }
    }

    suspend fun getMovieDetails(movieId: Int, language: String): AppResult<RemoteMovieDetails> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}/movie/${movieId}") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemoteMovieDetails>()
            }
        }
    }

    suspend fun getMovieVideos(movieId: Int, language: String): AppResult<RemoteVideoResponse> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}/movie/${movieId}/videos") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                        parameters.append("include_video_language", "$language,null")
                    }
                }.body<RemoteVideoResponse>()
            }
        }
    }

    suspend fun getMovieCredits(movieId: Int, language: String): AppResult<RemoteMovieCredits> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}/movie/${movieId}/credits") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemoteMovieCredits>()
            }
        }
    }
}
