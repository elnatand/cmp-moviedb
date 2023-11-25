package com.example.moviedb.core.data.movies.data_sources

import com.example.moviedb.core.data.model.API_KEY
import com.example.moviedb.core.data.model.TMDB_BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.MovieDetails
import com.example.moviedb.core.model.MoviesPage

class MoviesRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getMoviesPage(page:Int): List<Movie> {
        val moviesPages = httpClient
            .get("${TMDB_BASE_URL}movie/popular?api_key=$API_KEY"){
                url{
                    parameters.append("page", page.toString())
                }
            }
            .body<MoviesPage>()
        return moviesPages.results
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return httpClient
            .get("${TMDB_BASE_URL}/movie/${movieId}?api_key=$API_KEY")
            .body()
    }
}