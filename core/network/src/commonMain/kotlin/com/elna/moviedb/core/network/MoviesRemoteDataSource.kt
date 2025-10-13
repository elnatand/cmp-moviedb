package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.model.movies.RemoteMovieDetails
import com.elna.moviedb.core.network.model.movies.RemoteMoviesPage
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

    /**
         * Retrieve a page of now-playing movies.
         *
         * @param page The page number to fetch (1-based).
         * @param language ISO 639-1 language code to localize the results.
         * @return An AppResult containing the requested RemoteMoviesPage on success, or an error otherwise.
         */
        suspend fun getNowPlayingMoviesPage(page: Int, language: String) =
        fetchMoviesPage("movie/now_playing", page, language)

    /**
     * Retrieve detailed information for a specific movie from TMDB.
     *
     * @param movieId The TMDB movie identifier.
     * @param language The language code (ISO 639-1) to localize the returned details.
     * @return An AppResult containing the movie's RemoteMovieDetails on success, or an error result on failure.
     */
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

    /**
     * Fetches a page of movies from TMDB for the specified endpoint.
     *
     * @param path Relative TMDB API path segment (for example, "movie/popular" or "movie/top_rated").
     * @param page Page number to request.
     * @param language Language code to localize results.
     * @return An AppResult containing the fetched RemoteMoviesPage on success, or an error AppResult on failure.
     */
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