package data.movies

import model.Movie
import model.MovieDetails

interface MoviesRepository {
    suspend fun getMoviesPage(page: Int): List<Movie>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
}