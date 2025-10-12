package com.elna.moviedb.core.data.movies

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow


interface MoviesRepository {

    suspend fun observePopularMovies(): Flow<List<Movie>>
    suspend fun observeTopRatedMovies(): Flow<List<Movie>>
    suspend fun observeNowPlayingMovies(): Flow<List<Movie>>
    suspend fun loadPopularMoviesNextPage(): AppResult<Unit>
    suspend fun loadTopRatedMoviesNextPage(): AppResult<Unit>
    suspend fun loadNowPlayingMoviesNextPage(): AppResult<Unit>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    suspend fun refresh(): AppResult<List<Movie>>
    suspend fun clearMovies()
}