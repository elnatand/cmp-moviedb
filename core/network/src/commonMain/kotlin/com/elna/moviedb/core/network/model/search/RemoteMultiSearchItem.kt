package com.elna.moviedb.core.network.model.search

import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL
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
    @SerialName("known_for")
    val knownFor: List<RemoteMultiSearchItem>? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,

//    @SerialName("title")
//    val title: String?,
//    @SerialName("original_title")
//    val originalTitle: String?,
//    @SerialName("release_date")
//    val releaseDate: String?,
//    @SerialName("video")
//    val video: Boolean?,
//
//    @SerialName("name")
//    val name: String?,
//    @SerialName("original_name")
//    val originalName: String?,
//    @SerialName("first_air_date")
//    val firstAirDate: String?,
//    @SerialName("origin_country")
//    val originCountry: List<String>?,
//
//    @SerialName("profile_path")
//    val profilePath: String?,

//    @SerialName("gender")
//    val gender: Int?,


)


fun RemoteMultiSearchItem.toSearchResult(): SearchResultItem? {
    return when (mediaType) {
//        "movie" -> R
//
//
//        "tv" -> name?.let {
//            SearchResultItem.TvShowItem(
//                tvShow = TvShow(
//                    id = id,
//                    name = it,
//                    poster_path = "$TMDB_IMAGE_URL$posterPath"
//                ),
//                overview = overview,
//                firstAirDate = firstAirDate,
//                voteAverage = voteAverage,
//                voteCount = voteCount,
//                backdropPath = backdropPath?.let { path -> "$TMDB_IMAGE_URL$path" }
//            )
//        }

        else -> null
    }
}