package com.elna.moviedb.feature.movies.mappers

import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.network.model.search.RemoteSearchMovie
import com.elna.moviedb.feature.movies.model.Movie
import com.elna.moviedb.feature.movies.model.MovieDetails

fun RemoteSearchMovie.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath
    )
}

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