package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
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

    suspend fun getPopularMoviesPage(page: Int, language: String) =
        fetchMoviesPage("movie/popular", page, language)

    suspend fun getTopRatedMoviesPage(page: Int, language: String) =
        fetchMoviesPage("movie/top_rated", page, language)

    suspend fun getNowPlayingMoviesPage(page: Int, language: String) =
        fetchMoviesPage("movie/now_playing", page, language)

    suspend fun getMovieDetails(movieId: Int, language: String): AppResult<RemoteMovieDetails> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}movie/${movieId}") {
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
                httpClient.get("${TMDB_BASE_URL}movie/${movieId}/videos") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemoteVideoResponse>()
            }
        }
    }

    private suspend fun fetchMoviesPage(
        path: String,
        page: Int,
        language: String
    ): AppResult<RemoteMoviesPage> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}$path") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                    }
                }.body<RemoteMoviesPage>()
            }
        }
    }
}
