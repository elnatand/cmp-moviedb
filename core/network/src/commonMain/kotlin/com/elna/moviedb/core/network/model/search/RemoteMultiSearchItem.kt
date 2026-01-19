package com.elna.moviedb.core.network.model.search

import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.SearchResultItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteMultiSearchItem(
    @SerialName("id")
    val id: Int,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("profile_path")
    val profilePath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double?=null,
    @SerialName("vote_count")
    val voteCount: Int? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
)


fun RemoteMultiSearchItem.toSearchResult(): SearchResultItem? {
    return when (mediaType) {
        "movie" -> {
            val movieTitle = title ?: return null
            SearchResultItem.MovieItem(
                movie = Movie(
                    id = id,
                    title = movieTitle,
                    posterPath = posterPath
                ),
                overview = overview,
                releaseDate = releaseDate,
                voteAverage = voteAverage,
                voteCount = voteCount,
                backdropPath = backdropPath
            )
        }
        "tv" -> {
            val tvShowName = name ?: return null
            SearchResultItem.TvShowItem(
                tvShow = TvShow(
                    id = id,
                    name = tvShowName,
                    posterPath = posterPath
                ),
                overview = overview,
                firstAirDate = firstAirDate,
                voteAverage = voteAverage,
                voteCount = voteCount,
                backdropPath = backdropPath
            )
        }
        "person" -> {
            val personName = name ?: return null
            SearchResultItem.PersonItem(
                id = id,
                name = personName,
                knownForDepartment = knownForDepartment,
                profilePath = profilePath
            )
        }
        else -> null
    }
}