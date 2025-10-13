package com.elna.moviedb.core.data.movies

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow


interface MoviesRepository {

    suspend fun observePopularMovies(): Flow<List<Movie>>
    suspend fun observeTopRatedMovies(): Flow<List<Movie>>
    suspend fun observeNowPlayingMovies(): Flow<List<Movie>>
    /**
 * Loads the next page of popular movies and updates the observable popular movies stream.
 *
 * @return An AppResult containing `Unit` on success, or an error describing the failure.
 */
suspend fun loadPopularMoviesNextPage(): AppResult<Unit>
    /**
 * Loads the next page of top-rated movies into the repository's cache.
 *
 * @return An AppResult containing `Unit` on success, or an error describing the failure.
 */
suspend fun loadTopRatedMoviesNextPage(): AppResult<Unit>
    /**
 * Loads the next page of now-playing movies into the repository.
 *
 * Triggers fetching the following page of now-playing movie results and updates the repository's stored stream.
 *
 * @return An AppResult containing `Unit` on success, or an error describing the failure.
 */
suspend fun loadNowPlayingMoviesNextPage(): AppResult<Unit>
    /**
 * Fetches detailed information for the movie identified by the given ID.
 *
 * @param movieId The movie's unique identifier.
 * @return An AppResult containing the movie's details on success, or an error result on failure.
 */
suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails>
    /**
 * Refreshes cached movie data and returns the updated list of movies.
 *
 * @return An [AppResult] containing the list of refreshed [Movie] on success, or an error result describing the failure.
 */
suspend fun refresh(): AppResult<List<Movie>>
    /**
 * Clears all movies stored by the repository.
 *
 * Removes any cached or persisted movie entries so subsequent observers will reflect an empty or refreshed state.
 */
suspend fun clearMovies()
}