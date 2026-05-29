package com.elna.moviedb.feature.tvshows.data.mappers

import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShow
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShowDetails
import com.elna.moviedb.feature.tvshows.domain.model.TvShow
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails

fun RemoteTvShow.toDomain(): TvShow = TvShow(
    id = id,
    name = name,
    posterPath = posterPath
)

fun RemoteTvShowDetails.toDomain(): TvShowDetails = TvShowDetails(
    id = id,
    name = name,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    adult = adult,
    firstAirDate = firstAirDate,
    lastAirDate = lastAirDate,
    numberOfEpisodes = numberOfEpisodes,
    numberOfSeasons = numberOfSeasons,
    episodeRunTime = episodeRunTime,
    status = status,
    tagline = tagline,
    type = type,
    voteAverage = voteAverage,
    voteCount = voteCount,
    popularity = popularity,
    originalName = originalName,
    originalLanguage = originalLanguage,
    originCountry = originCountry,
    homepage = homepage,
    inProduction = inProduction,
    languages = languages,
    genres = genres?.map { it.name },
    networks = networks?.mapNotNull { it.name },
    productionCompanies = productionCompanies?.map { it.name },
    productionCountries = productionCountries?.map { it.name },
    spokenLanguages = spokenLanguages?.map { it.englishName },
    seasonsCount = seasons?.size,
    createdBy = createdBy?.mapNotNull { it.name },
    lastEpisodeName = lastEpisodeToAir?.name,
    lastEpisodeAirDate = lastEpisodeToAir?.airDate,
    nextEpisodeToAir = nextEpisodeToAir?.airDate,
    nextEpisodeAirDate = null
)
