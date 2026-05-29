package com.elna.moviedb.feature.movies

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.dto.credits.RemoteCredits
import com.elna.moviedb.core.network.dto.videos.RemoteVideoResponse
import com.elna.moviedb.feature.movies.datasources.MoviesRemoteDataSource
import com.elna.moviedb.feature.movies.model.RemoteMovieDetails
import com.elna.moviedb.feature.movies.model.RemoteMoviesPage

/**
 * Fake [MoviesRemoteDataSource] that returns configurable results and records call counts,
 * so repository tests can drive the offline-first / pagination paths without a real HTTP client.
 */
class FakeMoviesRemoteDataSource : MoviesRemoteDataSource {

    /** Page results keyed by TMDB path (e.g. "/movie/popular"); takes precedence when present. */
    val resultByPath = mutableMapOf<String, AppResult<RemoteMoviesPage>>()

    /** Page results keyed by requested page number; falls back to [defaultPage] when absent. */
    val pageByNumber = mutableMapOf<Int, AppResult<RemoteMoviesPage>>()
    var defaultPage: AppResult<RemoteMoviesPage> =
        AppResult.Success(RemoteMoviesPage(page = 1, totalPages = 1, results = emptyList()))

    var detailsResult: AppResult<RemoteMovieDetails> =
        AppResult.Success(remoteDetails(id = 1, title = "Movie"))
    var videosResult: AppResult<RemoteVideoResponse> =
        AppResult.Success(RemoteVideoResponse(id = 1, results = emptyList()))
    var creditsResult: AppResult<RemoteCredits> =
        AppResult.Success(RemoteCredits(id = 1, cast = emptyList()))

    var detailsCallCount = 0
        private set
    var videosCallCount = 0
        private set
    var creditsCallCount = 0
        private set
    val fetchedPages = mutableListOf<Int>()

    override suspend fun fetchMoviesPage(
        apiPath: String,
        page: Int,
        language: String
    ): AppResult<RemoteMoviesPage> {
        fetchedPages.add(page)
        return resultByPath[apiPath] ?: pageByNumber[page] ?: defaultPage
    }

    override suspend fun getMovieDetails(movieId: Int, language: String): AppResult<RemoteMovieDetails> {
        detailsCallCount++
        return detailsResult
    }

    override suspend fun getMovieVideos(movieId: Int, language: String): AppResult<RemoteVideoResponse> {
        videosCallCount++
        return videosResult
    }

    override suspend fun getMovieCredits(movieId: Int, language: String): AppResult<RemoteCredits> {
        creditsCallCount++
        return creditsResult
    }

    companion object {
        fun remoteDetails(id: Int, title: String) = RemoteMovieDetails(
            id = id,
            title = title,
            overview = "",
            posterPath = null,
            backdropPath = null,
            releaseDate = null,
            runtime = null,
            voteAverage = null,
            voteCount = null,
            adult = null,
            budget = null,
            revenue = null,
            homepage = null,
            imdbId = null,
            originalLanguage = null,
            originalTitle = null,
            popularity = null,
            status = null,
            tagline = null,
            genres = null,
            productionCompanies = null,
            productionCountries = null,
            spokenLanguages = null,
        )
    }
}
