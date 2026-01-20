package com.elna.moviedb.feature.movies.datasources

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.TmdbApiClient
import com.elna.moviedb.core.network.model.videos.RemoteVideoResponse
import com.elna.moviedb.feature.movies.model.RemoteMovieCredits
import com.elna.moviedb.feature.movies.model.RemoteMovieDetails
import com.elna.moviedb.feature.movies.model.RemoteMoviesPage


class MoviesRemoteDataSource(
    private val apiClient: TmdbApiClient
) {

    /**
     * Fetches a page of movies for any category from TMDB API.
     *
     * This generic method supports all movie categories.
     * New categories can be added without modifying this method.
     *
     * @param apiPath The TMDB API path (e.g., "/movie/popular", "/movie/top_rated")
     * @param page The page number to fetch
     * @param language The language code for the results
     * @return AppResult containing the movies page or an error
     */
    suspend fun fetchMoviesPage(
        apiPath: String,
        page: Int,
        language: String
    ): AppResult<RemoteMoviesPage> {
        return apiClient.get(
            path = apiPath,
            "page" to page.toString(),
            "language" to language
        )
    }

    suspend fun getMovieDetails(movieId: Int, language: String): AppResult<RemoteMovieDetails> {
        return apiClient.get(
            path = "/movie/$movieId",
            "language" to language
        )
    }

    suspend fun getMovieVideos(movieId: Int, language: String): AppResult<RemoteVideoResponse> {
        return apiClient.get(
            path = "/movie/$movieId/videos",
            "language" to language,
            "include_video_language" to "$language,null"
        )
    }

    suspend fun getMovieCredits(movieId: Int, language: String): AppResult<RemoteMovieCredits> {
        return apiClient.get(
            path = "/movie/$movieId/credits",
            "language" to language
        )
    }
}
