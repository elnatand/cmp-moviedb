package movies.data

import movies.data.data_sources.MoviesRemoteDataSource
import movies.model.Movie
import movies.model.MovieDetails

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource
) : MoviesRepository {

    override suspend fun getMoviesPage(): List<Movie> {
        return moviesRemoteDataSource.getMoviesPage()
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return moviesRemoteDataSource.getMovieDetails(movieId)
    }
}