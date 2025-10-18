package com.elna.moviedb.core.database

import com.elna.moviedb.core.database.model.CastMemberEntity
import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.MovieEntity
import com.elna.moviedb.core.database.model.VideoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for local data source operations related to movies.
 * Following Dependency Inversion Principle - repositories depend on this abstraction.
 */
interface MoviesLocalDataSource {
    fun getMoviesByCategoryAsFlow(category: String): Flow<List<MovieEntity>>
    suspend fun insertMoviesPage(movies: List<MovieEntity>)
    suspend fun getMoviesDetails(movieId: Int): MovieDetailsEntity?
    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity)
    suspend fun clearAllMovies()
    suspend fun getVideosForMovie(movieId: Int): List<VideoEntity>
    suspend fun insertVideos(videos: List<VideoEntity>)
    suspend fun deleteVideosForMovie(movieId: Int)
    suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity>
    suspend fun insertCastMembers(cast: List<CastMemberEntity>)
    suspend fun deleteCastForMovie(movieId: Int)
    suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>)
}

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

    override suspend fun getMoviesDetails(movieId: Int): MovieDetailsEntity? {
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

    override suspend fun insertCastMembers(cast: List<CastMemberEntity>) {
        movieDetailsDao.insertCastMembers(cast)
    }

    override suspend fun deleteCastForMovie(movieId: Int) {
        movieDetailsDao.deleteCastForMovie(movieId)
    }

    override suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>) {
        movieDetailsDao.replaceCastForMovie(movieId, cast)
    }
}