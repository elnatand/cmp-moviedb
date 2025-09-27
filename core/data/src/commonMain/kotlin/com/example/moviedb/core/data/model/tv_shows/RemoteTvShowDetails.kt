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
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("overview")
    val overview: String,
    @SerialName("adult")
    val adult: Boolean = false,
    @SerialName("backdrop_path")
    val backdrop_path: String?,
    @SerialName("created_by")
    val created_by: List<CreatedBy> = emptyList(),
    @SerialName("episode_run_time")
    val episode_run_time: List<Int> = emptyList(),
    @SerialName("first_air_date")
    val first_air_date: String?,
    @SerialName("genres")
    val genres: List<Genre> = emptyList(),
    @SerialName("homepage")
    val homepage: String?,
    @SerialName("in_production")
    val in_production: Boolean = false,
    @SerialName("languages")
    val languages: List<String> = emptyList(),
    @SerialName("last_air_date")
    val last_air_date: String?,
    @SerialName("last_episode_to_air")
    val last_episode_to_air: Episode?,
    @SerialName("next_episode_to_air")
    val next_episode_to_air: Episode?,
    @SerialName("networks")
    val networks: List<Network> = emptyList(),
    @SerialName("number_of_episodes")
    val number_of_episodes: Int = 0,
    @SerialName("number_of_seasons")
    val number_of_seasons: Int = 0,
    @SerialName("origin_country")
    val origin_country: List<String> = emptyList(),
    @SerialName("original_language")
    val original_language: String,
    @SerialName("original_name")
    val original_name: String,
    @SerialName("popularity")
    val popularity: Double = 0.0,
    @SerialName("poster_path")
    val poster_path: String?,
    @SerialName("production_companies")
    val production_companies: List<ProductionCompany> = emptyList(),
    @SerialName("production_countries")
    val production_countries: List<ProductionCountry> = emptyList(),
    @SerialName("seasons")
    val seasons: List<Season> = emptyList(),
    @SerialName("spoken_languages")
    val spoken_languages: List<SpokenLanguage> = emptyList(),
    @SerialName("status")
    val status: String?,
    @SerialName("tagline")
    val tagline: String?,
    @SerialName("type")
    val type: String?,
    @SerialName("vote_average")
    val vote_average: Double = 0.0,
    @SerialName("vote_count")
    val vote_count: Int = 0
)

@Serializable
data class CreatedBy(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("credit_id")
    val credit_id: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("gender")
    val gender: Int = 0,
    @SerialName("profile_path")
    val profile_path: String?
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
    val vote_average: Double = 0.0,
    @SerialName("vote_count")
    val vote_count: Int = 0,
    @SerialName("air_date")
    val air_date: String?,
    @SerialName("episode_number")
    val episode_number: Int = 0,
    @SerialName("production_code")
    val production_code: String = "",
    @SerialName("runtime")
    val runtime: Int = 0,
    @SerialName("season_number")
    val season_number: Int = 0,
    @SerialName("show_id")
    val show_id: Int = 0,
    @SerialName("still_path")
    val still_path: String?
)

@Serializable
data class Network(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("logo_path")
    val logo_path: String?,
    @SerialName("name")
    val name: String = "",
    @SerialName("origin_country")
    val origin_country: String = ""
)

@Serializable
data class Season(
    @SerialName("air_date")
    val air_date: String?,
    @SerialName("episode_count")
    val episode_count: Int = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("overview")
    val overview: String = "",
    @SerialName("poster_path")
    val poster_path: String?,
    @SerialName("season_number")
    val season_number: Int = 0,
    @SerialName("vote_average")
    val vote_average: Double = 0.0
)

fun RemoteTvShowDetails.toDomain() = TvShowDetails(
    id = id,
    name = name,
    overview = overview,
    posterPath = poster_path,
    backdropPath = backdrop_path,
    adult = adult,
    firstAirDate = first_air_date,
    lastAirDate = last_air_date,
    numberOfEpisodes = number_of_episodes,
    numberOfSeasons = number_of_seasons,
    episodeRunTime = episode_run_time,
    status = status,
    tagline = tagline,
    type = type,
    voteAverage = vote_average,
    voteCount = vote_count,
    popularity = popularity,
    originalName = original_name,
    originalLanguage = original_language,
    originCountry = origin_country,
    homepage = homepage,
    inProduction = in_production,
    languages = languages,
    genres = genres.map { it.name },
    networks = networks.map { it.name },
    productionCompanies = production_companies.map { it.name },
    productionCountries = production_countries.map { it.name },
    spokenLanguages = spoken_languages.map { it.english_name },
    seasonsCount = seasons.size,
    createdBy = created_by.map { it.name },
    lastEpisodeName = last_episode_to_air?.name,
    lastEpisodeAirDate = last_episode_to_air?.air_date,
    nextEpisodeName = next_episode_to_air?.name,
    nextEpisodeAirDate = next_episode_to_air?.air_date
)
