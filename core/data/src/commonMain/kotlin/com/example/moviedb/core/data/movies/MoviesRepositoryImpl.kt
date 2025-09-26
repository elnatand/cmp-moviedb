package com.example.moviedb.core.data.movies

import com.example.moviedb.core.model.MDResponse
import com.example.moviedb.core.data.model.movies.asEntity
import com.example.moviedb.core.data.model.movies.toEntity
import com.example.moviedb.core.data.movies.data_sources.MoviesLocalDataSource
import com.example.moviedb.core.data.movies.data_sources.MoviesRemoteDataSource
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource
) : MoviesRepository {

    private var currentPage = 0
    private var totalPages = 0

    private val _errorState = MutableStateFlow<MDResponse.Error?>(null)


    override suspend fun observeAllMovies(): Flow<MDResponse<List<Movie>>> {
        val localMoviesPageStream = moviesLocalDataSource.getAllMovies()

        // Load initial data if empty
        if (localMoviesPageStream.first().isEmpty()) {
            loadNextPage()
        }

        return combine(
            localMoviesPageStream,
            _errorState
        ) { movieEntities, error ->
            // Return error if present
            error?.let { return@combine it }

            // Return success with movie data
            MDResponse.Success(
                data = movieEntities.map {
                    Movie(
                        id = it.id,
                        title = it.title,
                        poster_path = it.poster_path
                    )
                }
            )
        }
    }


    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        val localMovieDetails = moviesLocalDataSource.getMoviesDetails(movieId)
        if (localMovieDetails == null) {
            when (val result = moviesRemoteDataSource.getMovieDetails(movieId)) {
                is MDResponse.Success -> {
                    moviesLocalDataSource.insertMovieDetails(result.data.toEntity())
                }

                is MDResponse.Error -> {
                    throw Exception(result.message)
                }
            }
        }
        return moviesLocalDataSource.getMoviesDetails(movieId)!!.toDomain()
    }

    override suspend fun loadNextPage() {

        _errorState.value = null

        val nextPage = currentPage + 1

        when (val result = moviesRemoteDataSource.getMoviesPage(nextPage)) {
            is MDResponse.Success -> {
                totalPages = result.data.total_pages
                val entities = result.data.results.map { it.asEntity(nextPage) }
                moviesLocalDataSource.insertMoviesPage(entities)
                currentPage = nextPage
            }

            is MDResponse.Error -> {
                _errorState.value = result
            }
        }
    }

    override suspend fun refresh(): MDResponse<List<Movie>> {

        currentPage = 0
        totalPages = 0
        _errorState.value = null


        loadNextPage()

        // Return result based on loading outcome
        return _errorState.value ?: run {
            // If no error, get the current data from local storage
            val localMovies = moviesLocalDataSource.getAllMovies().first()
            MDResponse.Success(
                data = localMovies.map {
                    Movie(
                        id = it.id,
                        title = it.title,
                        poster_path = it.poster_path
                    )
                }
            )
        }
    }
}
