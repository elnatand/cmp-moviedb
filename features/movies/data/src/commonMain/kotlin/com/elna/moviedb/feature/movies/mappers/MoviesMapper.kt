package com.elna.moviedb.feature.movies.mappers

import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.MovieEntity
import com.elna.moviedb.feature.movies.model.MovieCategory
import com.elna.moviedb.feature.movies.model.MovieDetails
import com.elna.moviedb.feature.movies.model.RemoteMovie
import com.elna.moviedb.feature.movies.model.RemoteMovieDetails
import kotlin.time.Clock

fun MovieDetailsEntity.toDomain(): MovieDetails = MovieDetails(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    runtime = runtime,
    voteAverage = voteAverage,
    voteCount = voteCount,
    adult = adult,
    budget = budget,
    revenue = revenue,
    homepage = homepage,
    imdbId = imdbId,
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    popularity = popularity,
    status = status,
    tagline = tagline,
    genres = genres?.split(",")?.filter { it.isNotBlank() },
    productionCompanies = productionCompanies?.split(",")?.filter { it.isNotBlank() },
    productionCountries = productionCountries?.split(",")?.filter { it.isNotBlank() },
    spokenLanguages = spokenLanguages?.split(",")?.filter { it.isNotBlank() }
)

/**
 * Maps domain MovieCategory to TMDB API endpoint paths.
 *
 * Following Clean Architecture - keeps domain layer independent of infrastructure.
 * This mapper lives in the network layer where TMDB-specific details belong.
 *
 * @return TMDB API movie category endpoint path (e.g., "movie/popular", "movie/top_rated")
 */
fun MovieCategory.toTmdbPath(): String = when (this) {
    MovieCategory.POPULAR -> "/movie/popular"
    MovieCategory.TOP_RATED -> "/movie/top_rated"
    MovieCategory.NOW_PLAYING -> "/movie/now_playing"
}


fun RemoteMovie.asEntity() = MovieEntity(
    id = id,
    timestamp = Clock.System.now().epochSeconds,
    title = title,
    posterPath = posterPath,
)


fun RemoteMovieDetails.asEntity() = MovieDetailsEntity(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    runtime = runtime,
    voteAverage = voteAverage,
    voteCount = voteCount,
    adult = adult,
    budget = budget,
    revenue = revenue,
    homepage = homepage,
    imdbId = imdbId,
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    popularity = popularity,
    status = status,
    tagline = tagline,
    genres = genres?.joinToString(",") { it.name },
    productionCompanies = productionCompanies?.joinToString(",") { it.name },
    productionCountries = productionCountries?.joinToString(",") { it.name },
    spokenLanguages = spokenLanguages?.joinToString(",") { it.englishName }
)

