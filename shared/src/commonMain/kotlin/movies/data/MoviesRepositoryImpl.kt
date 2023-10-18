package movies.data

import movies.data.data_sources.MoviesRemoteDataSource
import movies.model.Movie

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource
) : MoviesRepository {

    override suspend fun getMoviesPage(): List<Movie> {
        return moviesRemoteDataSource.getMoviesPage()
    }
}