package com.elna.moviedb.core.network.model.movies

import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class RemoteMovieDetails(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("overview")
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("runtime")
    val runtime: Int?,
    @SerialName("vote_average")
    val voteAverage: Double?,
    @SerialName("vote_count")
    val voteCount: Int?,
    @SerialName("adult")
    val adult: Boolean?,
    @SerialName("budget")
    val budget: Long?,
    @SerialName("revenue")
    val revenue: Long?,
    @SerialName("homepage")
    val homepage: String?,
    @SerialName("imdb_id")
    val imdbId: String?,
    @SerialName("original_language")
    val originalLanguage: String?,
    @SerialName("original_title")
    val originalTitle: String?,
    @SerialName("popularity")
    val popularity: Double?,
    @SerialName("status")
    val status: String?,
    @SerialName("tagline")
    val tagline: String?,
    @SerialName("genres")
    val genres: List<Genre>?,
    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompany>?,
    @SerialName("production_countries")
    val productionCountries: List<ProductionCountry>?,
    @SerialName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>?
)

@Serializable
data class Genre(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)

@Serializable
data class ProductionCompany(
    @SerialName("id")
    val id: Int,
    @SerialName("logo_path")
    val logoPath: String?,
    @SerialName("name")
    val name: String,
    @SerialName("origin_country")
    val originCountry: String
)

@Serializable
data class ProductionCountry(
    @SerialName("iso_3166_1")
    val iso31661: String,
    @SerialName("name")
    val name: String
)

@Serializable
data class SpokenLanguage(
    @SerialName("english_name")
    val englishName: String,
    @SerialName("iso_639_1")
    val iso6391: String,
    @SerialName("name")
    val name: String
)

fun RemoteMovieDetails.toEntity(): MovieDetailsEntity = MovieDetailsEntity(
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
