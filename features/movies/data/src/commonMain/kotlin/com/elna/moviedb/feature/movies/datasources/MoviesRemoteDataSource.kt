package com.elna.moviedb.feature.movies.datasources

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.TmdbApiClient
import com.elna.moviedb.core.network.dto.credits.RemoteCredits
import com.elna.moviedb.core.network.dto.videos.RemoteVideoResponse
import com.elna.moviedb.feature.movies.model.RemoteMovieDetails
import com.elna.moviedb.feature.movies.model.RemoteMoviesPage


/**
 * Remote source for movie data from the TMDB API.
 *
 * Declared as an interface (mirroring [com.elna.moviedb.feature.movies.datasources.MoviesLocalDataSource])
 * so the repository depends on an abstraction and can be unit-tested with a fake, without
 * standing up a real HTTP client.
 */
interface MoviesRemoteDataSource {
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
    ): AppResult<RemoteMoviesPage>

    suspend fun getMovieDetails(movieId: Int, language: String): AppResult<RemoteMovieDetails>

    suspend fun getMovieVideos(movieId: Int, language: String): AppResult<RemoteVideoResponse>

    suspend fun getMovieCredits(movieId: Int, language: String): AppResult<RemoteCredits>
}

class MoviesRemoteDataSourceImpl(
    private val apiClient: TmdbApiClient
) : MoviesRemoteDataSource {

    override suspend fun fetchMoviesPage(
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

    override suspend fun getMovieDetails(movieId: Int, language: String): AppResult<RemoteMovieDetails> {
        return apiClient.get(
            path = "/movie/$movieId",
            "language" to language
        )
    }

    override suspend fun getMovieVideos(movieId: Int, language: String): AppResult<RemoteVideoResponse> {
        // include_video_language expects ISO-639-1 codes ("en"), not the regional form
        // ("en-US") used for `language`; passing the regional form filters out all videos.
        val languageCode = language.substringBefore("-")
        return apiClient.get(
            path = "/movie/$movieId/videos",
            "language" to language,
            "include_video_language" to "$languageCode,null"
        )
    }

    override suspend fun getMovieCredits(movieId: Int, language: String): AppResult<RemoteCredits> {
        return apiClient.get(
            path = "/movie/$movieId/credits",
            "language" to language
        )
    }
}
