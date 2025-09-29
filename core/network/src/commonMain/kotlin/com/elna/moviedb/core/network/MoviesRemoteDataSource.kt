package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatcher
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.model.movies.RemoteMovieDetails
import com.elna.moviedb.core.network.model.movies.RemoteMoviesPage
import com.elna.moviedb.core.network.model.platformCountry
import com.elna.moviedb.core.network.model.platformLanguage
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
