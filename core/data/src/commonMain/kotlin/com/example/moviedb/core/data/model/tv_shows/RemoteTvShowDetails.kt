package com.example.moviedb.core.data.model.tv_shows


import com.example.moviedb.core.data.model.movies.Genre
import com.example.moviedb.core.data.model.movies.ProductionCompany
import com.example.moviedb.core.data.model.movies.ProductionCountry
import com.example.moviedb.core.data.model.movies.SpokenLanguage
import com.example.moviedb.core.model.TvShowDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteTvShowDetails(
    @SerialName("adult")
    val adult: Boolean = true,
    @SerialName("backdrop_path")
    val backdropPath: String = "",
    @SerialName("created_by")
    val createdBy: List<CreatedBy> = emptyList(),
    @SerialName("episode_run_time")
    val episodeRunTime: List<Int> = emptyList(),
    @SerialName("first_air_date")
    val firstAirDate: String = "",
    @SerialName("genres")
    val genres: List<Genre> = emptyList(),
    @SerialName("homepage")
    val homepage: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("in_production")
    val inProduction: Boolean = true,
    @SerialName("languages")
    val languages: List<String> = emptyList(),
    @SerialName("last_air_date")
    val lastAirDate: String = "",
    @SerialName("last_episode_to_air")
    val lastEpisodeToAir: Episode = Episode(),
    @SerialName("name")
    val name: String = "",
    @SerialName("next_episode_to_air")
    val nextEpisodeToAir: String? = "",
    @SerialName("networks")
    val networks: List<Network> = emptyList(),
    @SerialName("number_of_episodes")
    val numberOfEpisodes: Int = 0,
    @SerialName("number_of_seasons")
    val numberOfSeasons: Int = 0,
    @SerialName("origin_country")
    val originCountry: List<String> = emptyList(),
    @SerialName("original_language")
    val originalLanguage: String = "",
    @SerialName("original_name")
    val originalName: String = "",
    @SerialName("overview")
    val overview: String = "",
    @SerialName("popularity")
    val popularity: Double = 0.0,
    @SerialName("poster_path")
    val posterPath: String = "",
    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompany> = emptyList(),
    @SerialName("production_countries")
    val productionCountries: List<ProductionCountry> = emptyList(),
    @SerialName("seasons")
    val seasons: List<Season> = emptyList(),
    @SerialName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage> = emptyList(),
    @SerialName("status")
    val status: String = "",
    @SerialName("tagline")
    val tagline: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0
)

@Serializable
data class CreatedBy(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("credit_id")
    val creditId: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("gender")
    val gender: Int = 0,
    @SerialName("profile_path")
    val profilePath: String = ""
)

@Serializable
data class Episode(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("overview")
    val overview: String = "",
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    @SerialName("air_date")
    val airDate: String = "",
    @SerialName("episode_number")
    val episodeNumber: Int = 0,
    @SerialName("production_code")
    val productionCode: String = "",
    @SerialName("runtime")
    val runtime: Int = 0,
    @SerialName("season_number")
    val seasonNumber: Int = 0,
    @SerialName("show_id")
    val showId: Int = 0,
    @SerialName("still_path")
    val stillPath: String = ""
)

@Serializable
data class Network(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("logo_path")
    val logoPath: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("origin_country")
    val originCountry: String = ""
)

@Serializable
data class Season(
    @SerialName("air_date")
    val airDate: String = "",
    @SerialName("episode_count")
    val episodeCount: Int = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("overview")
    val overview: String = "",
    @SerialName("poster_path")
    val posterPath: String = "",
    @SerialName("season_number")
    val seasonNumber: Int = 0,
    @SerialName("vote_average")
    val voteAverage: Float = 0.0f
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
    genres = genres.map { it.name },
    networks = networks.map { it.name },
    productionCompanies = productionCompanies.map { it.name },
    productionCountries = productionCountries.map { it.name },
    spokenLanguages = spokenLanguages.map { it.english_name },
    seasonsCount = seasons.size,
    createdBy = createdBy.map { it.name },
    lastEpisodeName = lastEpisodeToAir.name,
    lastEpisodeAirDate = lastEpisodeToAir.airDate,
    nextEpisodeToAir = nextEpisodeToAir,
    nextEpisodeAirDate = ""
)
