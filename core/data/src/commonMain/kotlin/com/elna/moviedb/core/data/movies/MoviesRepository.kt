package com.elna.moviedb.core.data.movies

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieCategory
import com.elna.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for movie data operations.
 *
 * This interface follows the Open/Closed Principle by using category abstraction.
 * New movie categories can be added to [MovieCategory] enum without modifying this interface.
 */
interface MoviesRepository {

    /**
     * Observes movies for a specific category from local storage.
     *
     * Returns a flow of movies from the local cache. Automatically triggers
     * initial load if cache is empty for the given category.
     *
     * @param category The movie category to observe (e.g., POPULAR, TOP_RATED)
     * @return Flow emitting list of movies for the category
     */
    suspend fun observeMovies(category: MovieCategory): Flow<List<Movie>>

    /**
     * Loads the next page of movies for a specific category from the remote API.
     *
     * @param category The movie category to load (e.g., POPULAR, TOP_RATED)
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit>

    /**
     * Retrieves detailed information for a specific movie.
     *
     * @param movieId The unique identifier of the movie
     * @return AppResult<MovieDetails> Success with movie details or Error if fetch failed
     */
    suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails>

    /**
     * Clears all cached movies and reloads initial pages for all categories.
     * Used by language coordinator when language changes.
     */
    suspend fun clearAndReload()
}