package movies.data

import movies.model.Movie
import movies.model.MovieDetails

interface MoviesRepository {
    suspend fun getMoviesPage(): List<Movie>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
}