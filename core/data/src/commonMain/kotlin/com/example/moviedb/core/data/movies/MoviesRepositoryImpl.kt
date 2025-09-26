package com.example.moviedb.core.data.movies

import com.example.moviedb.core.data.model.movies.RemoteMovie
import com.example.moviedb.core.model.MDResponse
import com.example.moviedb.core.data.model.movies.asEntity
import com.example.moviedb.core.data.model.movies.toEntity
import com.example.moviedb.core.data.movies.data_sources.MoviesLocalDataSource
import com.example.moviedb.core.data.movies.data_sources.MoviesRemoteDataSource
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.MovieDetails
import com.example.moviedb.core.model.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource
) : MoviesRepository {

    private var currentPage = 0
    private var totalPages = 0


    override suspend fun observeAllMovies(): Flow<MDResponse<List<Movie>>> {
        val localMoviesPageStream = moviesLocalDataSource.getAllMovies()
        if (localMoviesPageStream.first().isEmpty()) {
            val response = loadInternalNextPage()
            when (response) {
                is MDResponse.Error -> return flowOf(response)
                is MDResponse.Success -> {
                    val entities = response.data.map { it.asEntity(currentPage) }
                    moviesLocalDataSource.insertMoviesPage(entities)
                }
            }
        }

        return localMoviesPageStream.map { list ->
            MDResponse.Success(
                data = list.map {
                    currentPage = it.page

                    Movie(
                        id = it.id,
                        title = it.title,
                        poster_path = it.poster_path
                    )
                }
            )
        }
    }

    private suspend fun loadInternalNextPage(): MDResponse<List<RemoteMovie>> {
        if (currentPage != 0 && currentPage < totalPages) {
            return MDResponse.Error("No more pages to load or loading in progress")
        }

        val nextPage = currentPage + 1

        return when (val result = moviesRemoteDataSource.getMoviesPage(nextPage)) {
            is MDResponse.Success -> {
                totalPages = result.data.total_pages

                val movies = result.data.results

                currentPage = nextPage
                MDResponse.Success(movies)
            }

            is MDResponse.Error -> result
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

    }

    override suspend fun refresh(): MDResponse<List<Movie>> {
        currentPage = 0
        totalPages = 0

        // Note: We might need to add a clear method to the local data source
        // For now, just reset pagination state and load fresh data

        return loadNextPage()
    }
}
