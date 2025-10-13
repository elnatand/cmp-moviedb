package com.elna.moviedb.core.data.model

import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.MovieEntity
import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL
import com.elna.moviedb.core.network.model.movies.RemoteMovie
import com.elna.moviedb.core.network.model.movies.RemoteMovieDetails
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun RemoteMovie.asEntity() = MovieEntity(
    id = id,
    timestamp = Clock.System.now().epochSeconds,
    title = title,
    posterPath = "$TMDB_IMAGE_URL$posterPath"
)


fun RemoteMovieDetails.asEntity() = MovieDetailsEntity(
    id = id,
    title = title,
    overview = overview,
    posterPath = "$TMDB_IMAGE_URL$posterPath",
    backdropPath = "$TMDB_IMAGE_URL$backdropPath",
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
