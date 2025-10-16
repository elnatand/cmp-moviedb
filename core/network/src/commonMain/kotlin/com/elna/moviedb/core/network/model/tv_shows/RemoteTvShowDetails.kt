package com.elna.moviedb.core.network.model.tv_shows


import com.elna.moviedb.core.network.model.movies.Genre
import com.elna.moviedb.core.network.model.movies.ProductionCompany
import com.elna.moviedb.core.network.model.movies.ProductionCountry
import com.elna.moviedb.core.network.model.movies.SpokenLanguage
import com.elna.moviedb.core.model.TvShowDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteTvShowDetails(
    @SerialName("adult")
    val adult: Boolean?,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    @SerialName("created_by")
    val createdBy: List<CreatedBy>?,
    @SerialName("episode_run_time")
    val episodeRunTime: List<Int>?,
    @SerialName("first_air_date")
    val firstAirDate: String?,
    @SerialName("genres")
    val genres: List<Genre>?,
    @SerialName("homepage")
    val homepage: String?,
    @SerialName("id")
    val id: Int?,
    @SerialName("in_production")
    val inProduction: Boolean?,
    @SerialName("languages")
    val languages: List<String>?,
    @SerialName("last_air_date")
    val lastAirDate: String?,
    @SerialName("last_episode_to_air")
    val lastEpisodeToAir: Episode?,
    @SerialName("name")
    val name: String?,
    @SerialName("next_episode_to_air")
    val nextEpisodeToAir: Episode?,
    @SerialName("networks")
    val networks: List<Network>?,
    @SerialName("number_of_episodes")
    val numberOfEpisodes: Int?,
    @SerialName("number_of_seasons")
    val numberOfSeasons: Int?,
    @SerialName("origin_country")
    val originCountry: List<String>?,
    @SerialName("original_language")
    val originalLanguage: String?,
    @SerialName("original_name")
    val originalName: String?,
    @SerialName("overview")
    val overview: String?,
    @SerialName("popularity")
    val popularity: Double?,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompany>?,
    @SerialName("production_countries")
    val productionCountries: List<ProductionCountry>?,
    @SerialName("seasons")
    val seasons: List<Season>?,
    @SerialName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>?,
    @SerialName("status")
    val status: String?,
    @SerialName("tagline")
    val tagline: String?,
    @SerialName("type")
    val type: String?,
    @SerialName("vote_average")
    val voteAverage: Double?,
    @SerialName("vote_count")
    val voteCount: Int?
)

@Serializable
data class CreatedBy(
    @SerialName("id")
    val id: Int?,
    @SerialName("credit_id")
    val creditId: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("gender")
    val gender: Int?,
    @SerialName("profile_path")
    val profilePath: String?
)

@Serializable
data class Episode(
    @SerialName("id")
    val id: Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("overview")
    val overview: String?,
    @SerialName("vote_average")
    val voteAverage: Double?,
    @SerialName("vote_count")
    val voteCount: Int?,
    @SerialName("air_date")
    val airDate: String?,
    @SerialName("episode_number")
    val episodeNumber: Int?,
    @SerialName("production_code")
    val productionCode: String?,
    @SerialName("runtime")
    val runtime: Int?,
    @SerialName("season_number")
    val seasonNumber: Int?,
    @SerialName("show_id")
    val showId: Int?,
    @SerialName("still_path")
    val stillPath: String?
)

@Serializable
data class Network(
    @SerialName("id")
    val id: Int?,
    @SerialName("logo_path")
    val logoPath: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("origin_country")
    val originCountry: String?
)

@Serializable
data class Season(
    @SerialName("air_date")
    val airDate: String?,
    @SerialName("episode_count")
    val episodeCount: Int?,
    @SerialName("id")
    val id: Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("overview")
    val overview: String?,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("season_number")
    val seasonNumber: Int?,
    @SerialName("vote_average")
    val voteAverage: Float?
)

fun RemoteTvShowDetails.toDomain() = TvShowDetails(
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
