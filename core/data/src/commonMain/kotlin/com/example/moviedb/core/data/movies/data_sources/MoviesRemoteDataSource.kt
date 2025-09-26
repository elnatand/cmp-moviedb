package com.example.moviedb.core.data.movies.data_sources

import com.example.moviedb.core.common.AppDispatcher
import com.example.moviedb.core.model.AppResult
import com.example.moviedb.core.data.model.TMDB_API_KEY
import com.example.moviedb.core.data.model.movies.RemoteMoviesPage
import com.example.moviedb.core.data.model.TMDB_BASE_URL
import com.example.moviedb.core.data.model.movies.RemoteMovieDetails
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext


class MoviesRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatcher: AppDispatcher
) {
    suspend fun getMoviesPage(page: Int): AppResult<RemoteMoviesPage> {
        return try {
            val moviesPages = withContext(appDispatcher.getDispatcher()) {
                httpClient
                    .get("${TMDB_BASE_URL}movie/popular?api_key=$TMDB_API_KEY") {
                        url { parameters.append("page", page.toString()) }
                    }.body<RemoteMoviesPage>()
            }
            AppResult.Success(moviesPages)
        } catch (e: Exception) {
            AppResult.Error(
                message = e.message ?: "Unknown error occurred",
                throwable = e
            )
        }
    }

    suspend fun getMovieDetails(movieId: Int): AppResult<RemoteMovieDetails> {
        return try {
            val movieDetails = httpClient
                .get("${TMDB_BASE_URL}movie/${movieId}?api_key=$TMDB_API_KEY")
                .body<RemoteMovieDetails>()
            AppResult.Success(movieDetails)
        } catch (e: Exception) {
            AppResult.Error(
                message = e.message ?: "Unknown error occurred",
                throwable = e
            )
        }
    }
}