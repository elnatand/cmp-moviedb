package com.example.moviedb.core.data.movies.data_sources

import com.example.moviedb.core.common.AppDispatcher
import com.example.moviedb.core.data.model.TMDB_API_KEY
import com.example.moviedb.core.data.model.TMDB_BASE_URL
import com.example.moviedb.core.data.model.movies.RemoteMovieDetails
import com.example.moviedb.core.data.model.movies.RemoteMoviesPage
import com.example.moviedb.core.data.model.platformCountry
import com.example.moviedb.core.data.model.platformLanguage
import com.example.moviedb.core.model.AppResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext


class MoviesRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatcher: AppDispatcher
) {

    private val language = "$platformLanguage-$platformCountry"
    suspend fun getPopularMoviesPage(page: Int): AppResult<RemoteMoviesPage> {
        return try {
            val moviesPages = withContext(appDispatcher.getDispatcher()) {
                httpClient
                    .get("${TMDB_BASE_URL}movie/popular") {
                        url {
                            parameters.append("api_key", TMDB_API_KEY)
                            parameters.append("page", page.toString())
                            parameters.append("language", language)
                        }
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
                .get("${TMDB_BASE_URL}movie/${movieId}"){
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }
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
