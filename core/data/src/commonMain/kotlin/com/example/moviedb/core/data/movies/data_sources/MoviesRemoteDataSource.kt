package com.example.moviedb.core.data.movies.data_sources

import com.example.moviedb.core.data.model.API_KEY
import com.example.moviedb.core.data.model.NetworkMovie
import com.example.moviedb.core.data.model.RemoteMoviesPage
import com.example.moviedb.core.data.model.TMDB_BASE_URL
import com.example.moviedb.core.data.model.NetworkMovieDetails
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MoviesRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getMoviesPage(page:Int): List<NetworkMovie> {
        val moviesPages = httpClient
            .get("${TMDB_BASE_URL}movie/popular?api_key=$API_KEY"){
                url{
                    parameters.append("page", page.toString())
                }
            }
            .body<RemoteMoviesPage>()
        return moviesPages.results
    }

    suspend fun getMovieDetails(movieId: Int): NetworkMovieDetails {
        return httpClient
            .get("${TMDB_BASE_URL}movie/${movieId}?api_key=$API_KEY")
            .body()
    }
}