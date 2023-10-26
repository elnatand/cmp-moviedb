package features.movies.data

import features.movies.model.Movie
import features.movies.model.MovieDetails
import features.movies.model.MoviesPage

interface MoviesRepository {
    suspend fun getMoviesPage(): List<Movie>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
}