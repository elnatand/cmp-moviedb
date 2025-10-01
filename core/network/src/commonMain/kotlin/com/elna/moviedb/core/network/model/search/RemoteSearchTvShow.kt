package com.elna.moviedb.core.network.model.search

import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteSearchTvShow(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("original_name")
    val originalName: String?,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    @SerialName("overview")
    val overview: String?,
    @SerialName("first_air_date")
    val firstAirDate: String?,
    @SerialName("vote_average")
    val voteAverage: Double?,
    @SerialName("vote_count")
    val voteCount: Int?,
    @SerialName("popularity")
    val popularity: Double?,
    @SerialName("adult")
    val adult: Boolean?,
    @SerialName("genre_ids")
    val genreIds: List<Int>?,
    @SerialName("origin_country")
    val originCountry: List<String>?,
    @SerialName("original_language")
    val originalLanguage: String?
)

fun RemoteSearchTvShow.toDomain(): TvShow {
    return TvShow(
        id = id,
        name = name,
        poster_path = "$TMDB_IMAGE_URL$posterPath"
    )
}

fun RemoteSearchTvShow.toSearchResult(): SearchResultItem.TvShowItem {
    return SearchResultItem.TvShowItem(
        tvShow = TvShow(
            id = id,
            name = name,
            poster_path = "$TMDB_IMAGE_URL$posterPath"
        ),
        overview = overview,
        firstAirDate = firstAirDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        backdropPath = backdropPath?.let { "$TMDB_IMAGE_URL$it" }
    )
}