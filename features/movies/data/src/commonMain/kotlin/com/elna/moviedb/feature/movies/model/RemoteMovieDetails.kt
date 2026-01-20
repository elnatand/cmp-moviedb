package com.elna.moviedb.feature.movies.model

import com.elna.moviedb.core.network.model.shared.Genre
import com.elna.moviedb.core.network.model.shared.ProductionCompany
import com.elna.moviedb.core.network.model.shared.ProductionCountry
import com.elna.moviedb.core.network.model.shared.SpokenLanguage
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
