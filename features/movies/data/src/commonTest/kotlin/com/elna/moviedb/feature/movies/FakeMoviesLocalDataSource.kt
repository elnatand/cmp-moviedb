package com.elna.moviedb.feature.movies

import com.elna.moviedb.core.database.model.CastMemberEntity
import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.MovieEntity
import com.elna.moviedb.core.database.model.VideoEntity
import com.elna.moviedb.feature.movies.datasources.MoviesLocalDataSource
import com.elna.moviedb.feature.movies.model.MovieCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory [MoviesLocalDataSource] for tests. Faithfully stores whatever the repository writes
 * (so a save followed by a read round-trips), and records the entities inserted for assertions.
 */
class FakeMoviesLocalDataSource : MoviesLocalDataSource {

    private val moviesByCategory =
        MovieCategory.entries.associateWith { MutableStateFlow<List<MovieEntity>>(emptyList()) }

    /** Every movie-list entity ever inserted, in insertion order — for position assertions. */
    val insertedMovies = mutableListOf<MovieEntity>()

    private val detailsById = mutableMapOf<Int, MovieDetailsEntity>()
    private val videosById = mutableMapOf<Int, List<VideoEntity>>()
    private val castById = mutableMapOf<Int, List<CastMemberEntity>>()

    var clearMoviesListCount = 0
        private set
    var clearMovieDetailsCount = 0
        private set

    override fun getMoviesByCategoryAsFlow(category: MovieCategory): Flow<List<MovieEntity>> =
        moviesByCategory.getValue(category).map { list -> list.sortedBy { it.position } }

    override suspend fun insertMoviesPage(movies: List<MovieEntity>) {
        insertedMovies += movies
        movies.groupBy { it.category }.forEach { (categoryName, entities) ->
            val category = MovieCategory.valueOf(categoryName)
            val flow = moviesByCategory.getValue(category)
            flow.value = flow.value + entities
        }
    }

    override suspend fun clearMoviesList() {
        clearMoviesListCount++
        moviesByCategory.values.forEach { it.value = emptyList() }
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetailsEntity? = detailsById[movieId]

    override suspend fun saveMovieDetailsWithRelations(
        details: MovieDetailsEntity,
        videos: List<VideoEntity>,
        cast: List<CastMemberEntity>
    ) {
        detailsById[details.id] = details
        videosById[details.id] = videos
        castById[details.id] = cast
    }

    override suspend fun clearMovieDetails() {
        clearMovieDetailsCount++
        detailsById.clear()
    }

    override suspend fun getVideosForMovie(movieId: Int): List<VideoEntity> =
        videosById[movieId] ?: emptyList()

    override suspend fun clearAllVideos() {
        videosById.clear()
    }

    override suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity> =
        castById[movieId] ?: emptyList()

    override suspend fun clearAllCast() {
        castById.clear()
    }
}
