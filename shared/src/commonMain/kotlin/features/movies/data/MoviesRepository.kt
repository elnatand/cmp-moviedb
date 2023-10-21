package features.movies.data

import features.movies.model.Movie
import features.movies.model.MovieDetails

interface MoviesRepository {
    suspend fun getMoviesPage(): List<Movie>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
}