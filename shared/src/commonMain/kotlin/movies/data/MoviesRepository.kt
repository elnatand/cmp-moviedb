package movies.data

import movies.model.Movie

interface MoviesRepository {

    suspend fun getMoviesPage(): List<Movie>
    fun onCleared()
}