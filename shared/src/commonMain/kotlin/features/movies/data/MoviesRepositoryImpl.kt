package features.movies.data

import features.movies.data.data_sources.MoviesRemoteDataSource
import features.movies.model.Movie
import features.movies.model.MovieDetails

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource
) : MoviesRepository {

    override suspend fun getMoviesPage(page:Int): List<Movie> {
        return moviesRemoteDataSource.getMoviesPage(page)
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return moviesRemoteDataSource.getMovieDetails(movieId)
    }
}