package com.elna.moviedb.feature.movies.mappers

import com.elna.moviedb.core.database.model.CastMemberEntity
import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.MovieEntity
import com.elna.moviedb.core.model.CastMember
import com.elna.moviedb.feature.movies.model.Movie
import com.elna.moviedb.feature.movies.model.MovieCategory
import com.elna.moviedb.feature.movies.model.MovieDetails
import com.elna.moviedb.feature.movies.model.RemoteMovie
import com.elna.moviedb.feature.movies.model.RemoteMovieDetails
import kotlin.time.Clock

/**
 * Delimiter for persisting list fields (genres, companies, ...) as a single string column.
 * Uses the ASCII Unit Separator (U+001F) instead of a comma so values that themselves
 * contain commas (e.g. "Columbia Pictures Corporation, Ltd.") survive the round-trip.
 */
private const val LIST_DELIMITER = "\u001F"

fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterPath = posterPath
)

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
    genres = genres?.split(LIST_DELIMITER)?.filter { it.isNotBlank() },
    productionCompanies = productionCompanies?.split(LIST_DELIMITER)?.filter { it.isNotBlank() },
    productionCountries = productionCountries?.split(LIST_DELIMITER)?.filter { it.isNotBlank() },
    spokenLanguages = spokenLanguages?.split(LIST_DELIMITER)?.filter { it.isNotBlank() }
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


fun RemoteMovieDetails.toDomain(): MovieDetails = MovieDetails(
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
    genres = genres?.map { it.name },
    productionCompanies = productionCompanies?.map { it.name },
    productionCountries = productionCountries?.map { it.name },
    spokenLanguages = spokenLanguages?.map { it.englishName }
)

fun MovieDetails.asEntity(): MovieDetailsEntity = MovieDetailsEntity(
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
    genres = genres?.joinToString(LIST_DELIMITER),
    productionCompanies = productionCompanies?.joinToString(LIST_DELIMITER),
    productionCountries = productionCountries?.joinToString(LIST_DELIMITER),
    spokenLanguages = spokenLanguages?.joinToString(LIST_DELIMITER)
)

fun CastMember.asEntity(movieId: Int): CastMemberEntity = CastMemberEntity(
    movieId = movieId,
    personId = id,
    name = name,
    character = character,
    profilePath = profilePath,
    order = order
)

