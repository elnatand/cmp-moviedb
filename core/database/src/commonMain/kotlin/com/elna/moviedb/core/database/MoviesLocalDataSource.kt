package com.elna.moviedb.core.database

import com.elna.moviedb.core.database.model.CastMemberEntity
import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.MovieEntity
import com.elna.moviedb.core.database.model.VideoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for movies list operations (categories, pagination).
 * Following Interface Segregation Principle - clients only depend on what they need.
 *
 * Use this interface for:
 * - Fetching paginated movie lists by category
 * - Inserting new movie pages
 * - Clearing all cached movies
 */
interface MoviesListDataSource {
    /**
     * Observes movies for a specific category as a reactive flow.
     * @param category The category name (e.g., "POPULAR", "TOP_RATED")
     * @return Flow emitting list of movies for the category
     */
    fun getMoviesByCategoryAsFlow(category: String): Flow<List<MovieEntity>>

    /**
     * Inserts a page of movies into the local database.
     * @param movies List of movie entities to insert
     */
    suspend fun insertMoviesPage(movies: List<MovieEntity>)

    /**
     * Clears all cached movie data (list, details, videos, cast).
     * Used when refreshing or changing language.
     */
    suspend fun clearAllMovies()
}

/**
 * Interface for movie details operations.
 * Following Interface Segregation Principle - focused on movie detail entity only.
 *
 * Use this interface for:
 * - Fetching detailed movie information
 * - Caching movie details
 */
interface MovieDetailsDataSource {
    /**
     * Gets movie details from cache.
     * @param movieId The movie's unique identifier
     * @return MovieDetailsEntity if cached, null otherwise
     */
    suspend fun getMovieDetails(movieId: Int): MovieDetailsEntity?

    /**
     * Saves movie details to cache.
     * @param movieDetails The movie details entity to save
     */
    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity)
}

/**
 * Interface for movie videos/trailers operations.
 * Following Interface Segregation Principle - focused on video entities only.
 *
 * Use this interface for:
 * - Managing movie trailers and video clips
 * - Cache management for videos
 */
interface MovieVideosDataSource {
    /**
     * Gets all videos for a specific movie.
     * @param movieId The movie's unique identifier
     * @return List of video entities (trailers, teasers, etc.)
     */
    suspend fun getVideosForMovie(movieId: Int): List<VideoEntity>

    /**
     * Saves videos to cache.
     * @param videos List of video entities to save
     */
    suspend fun insertVideos(videos: List<VideoEntity>)

    /**
     * Deletes all videos for a specific movie.
     * Used before inserting fresh video data.
     * @param movieId The movie's unique identifier
     */
    suspend fun deleteVideosForMovie(movieId: Int)
}

/**
 * Interface for movie cast operations.
 * Following Interface Segregation Principle - focused on cast entities only.
 *
 * Use this interface for:
 * - Managing movie cast information
 * - Cache management for cast members
 */
interface MovieCastDataSource {
    /**
     * Gets all cast members for a specific movie.
     * @param movieId The movie's unique identifier
     * @return List of cast member entities sorted by order
     */
    suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity>

    /**
     * Atomically replaces all cast for a movie.
     * Deletes existing cast and inserts new cast in a transaction.
     * @param movieId The movie's unique identifier
     * @param cast New list of cast members
     */
    suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>)
}

/**
 * Composite interface combining all movie data source interfaces.
 * Following Interface Segregation Principle - clients can depend on focused interfaces
 * or this composite if they need multiple data sources.
 *
 * Use this interface when:
 * - You need access to multiple movie data operations
 * - You're implementing a general-purpose data layer
 *
 * Prefer using focused interfaces (MoviesListDataSource, MovieDetailsDataSource, etc.)
 * when you only need specific operations.
 */
interface MoviesLocalDataSource :
    MoviesListDataSource,
    MovieDetailsDataSource,
    MovieVideosDataSource,
    MovieCastDataSource

/**
 * Implementation of MoviesLocalDataSource using Room DAOs.
 */
class MoviesLocalDataSourceImpl(
    private val movieDao: MovieDao,
    private val movieDetailsDao: MovieDetailsDao,
) : MoviesLocalDataSource {

    override fun getMoviesByCategoryAsFlow(category: String): Flow<List<MovieEntity>> {
        return movieDao.getMoviesByCategoryAsFlow(category)
    }

    override suspend fun insertMoviesPage(movies: List<MovieEntity>) {
        movies.forEach {
            movieDao.insertMovie(it)
        }
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetailsEntity? {
        return movieDetailsDao.getMovieDetails(movieId)
    }

    override suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity) {
        movieDetailsDao.insertMovieDetails(movieDetails)
    }

    override suspend fun clearAllMovies() {
        movieDao.clearAllMovies()
        movieDetailsDao.clearAllMovieDetails()
        movieDetailsDao.clearAllCast()
    }

    override suspend fun getVideosForMovie(movieId: Int): List<VideoEntity> {
        return movieDetailsDao.getVideosForMovie(movieId)
    }

    override suspend fun insertVideos(videos: List<VideoEntity>) {
        movieDetailsDao.insertVideos(videos)
    }

    override suspend fun deleteVideosForMovie(movieId: Int) {
        movieDetailsDao.deleteVideosForMovie(movieId)
    }

    override suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity> {
        return movieDetailsDao.getCastForMovie(movieId)
    }

    override suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>) {
        movieDetailsDao.replaceCastForMovie(movieId, cast)
    }
}