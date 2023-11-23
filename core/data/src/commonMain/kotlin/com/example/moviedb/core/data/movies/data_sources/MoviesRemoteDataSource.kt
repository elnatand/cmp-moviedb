package com.example.moviedb.core.data.movies.data_sources

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
            .get("https://api.themoviedb.org/3/movie/popular?api_key=fe3e15709f26d5df026b17a743dbd529"){
                url{
                    parameters.append("page", page.toString())
                }
            }
            .body<MoviesPage>()
        return moviesPages.results
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return httpClient
            .get("https://api.themoviedb.org/3/movie/${movieId}?api_key=fe3e15709f26d5df026b17a743dbd529")
            .body()
    }
}