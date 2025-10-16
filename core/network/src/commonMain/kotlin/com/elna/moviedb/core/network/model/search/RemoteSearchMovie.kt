package com.elna.moviedb.core.network.model.search

import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.SearchResultItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteSearchMovie(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("original_title")
    val originalTitle: String?,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    @SerialName("overview")
    val overview: String?,
    @SerialName("release_date")
    val releaseDate: String?,
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
    @SerialName("original_language")
    val originalLanguage: String?,
    @SerialName("video")
    val video: Boolean?
)

fun RemoteSearchMovie.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath
    )
}

fun RemoteSearchMovie.toSearchResult(): SearchResultItem.MovieItem {
    return SearchResultItem.MovieItem(
        movie = Movie(
            id = id,
            title = title,
            posterPath = posterPath
        ),
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        backdropPath = backdropPath
    )
}