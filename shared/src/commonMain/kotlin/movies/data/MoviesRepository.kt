package movies.data

import movies.data.data_sources.MoviesRemoteDataSource
import movies.model.Movie

class MoviesRepository {
    private val moviesRemoteDataSource: MoviesRemoteDataSource = MoviesRemoteDataSource()

    suspend fun getMoviesPage(): List<Movie> {
        return moviesRemoteDataSource.getMoviesPage()
    }

    fun onCleared() {
        moviesRemoteDataSource.onCleared()
    }
}