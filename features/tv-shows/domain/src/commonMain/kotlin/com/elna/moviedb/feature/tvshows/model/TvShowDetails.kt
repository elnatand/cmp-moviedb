package com.elna.moviedb.feature.tvshows.model

import com.elna.moviedb.core.model.CastMember
import com.elna.moviedb.core.model.Video

data class TvShowDetails(
    val id: Int?,
    val name: String?,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val adult: Boolean?,
    val firstAirDate: String?,
    val lastAirDate: String?,
    val numberOfEpisodes: Int?,
    val numberOfSeasons: Int?,
    val episodeRunTime: List<Int>?,
    val status: String?,
    val tagline: String?,
    val type: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val popularity: Double?,
    val originalName: String?,
    val originalLanguage: String?,
    val originCountry: List<String>?,
    val homepage: String?,
    val inProduction: Boolean?,
    val languages: List<String>?,
    val genres: List<String>?,
    val networks: List<String>?,
    val productionCompanies: List<String>?,
    val productionCountries: List<String>?,
    val spokenLanguages: List<String>?,
    val seasonsCount: Int?,
    val createdBy: List<String>?,
    val lastEpisodeName: String?,
    val lastEpisodeAirDate: String?,
    val nextEpisodeToAir: String?,
    val nextEpisodeAirDate: String?,
    val trailers: List<Video>? = null,
    val cast: List<CastMember>? = null
)