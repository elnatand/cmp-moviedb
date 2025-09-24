package com.example.moviedb.core.data.movies.data_sources

import com.example.moviedb.core.common.AppDispatcher
import com.example.moviedb.core.data.model.TMDB_API_KEY
import com.example.moviedb.core.data.model.movies.NetworkMovie
import com.example.moviedb.core.data.model.movies.RemoteMoviesPage
import com.example.moviedb.core.data.model.TMDB_BASE_URL
import com.example.moviedb.core.data.model.movies.NetworkMovieDetails
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext


class MoviesRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatcher: AppDispatcher
) {
    suspend fun getMoviesPage(page: Int): List<NetworkMovie> {
        val moviesPages = withContext(appDispatcher.getDispatcher()) {
            httpClient
                .get("${TMDB_BASE_URL}movie/popular?api_key=$TMDB_API_KEY") {
                    url { parameters.append("page", page.toString()) }
                }.body<RemoteMoviesPage>()
        }

        return moviesPages.results
    }

    suspend fun getMovieDetails(movieId: Int): NetworkMovieDetails {
        return httpClient
            .get("${TMDB_BASE_URL}movie/${movieId}?api_key=$TMDB_API_KEY")
            .body()
    }
}