package data.movies

import data.movies.MoviesRepository
import data.movies.data_sources.MoviesRemoteDataSource
import model.Movie
import model.MovieDetails

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