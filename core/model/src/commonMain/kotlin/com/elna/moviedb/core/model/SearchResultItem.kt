package com.elna.moviedb.core.model

sealed class SearchResultItem {
    abstract val overview: String?
    abstract val voteAverage: Double?
    abstract val voteCount: Int?
    abstract val backdropPath: String?

    data class MovieItem(
        val movie: Movie,
        override val overview: String?,
        val releaseDate: String?,
        override val voteAverage: Double?,
        override val voteCount: Int?,
        override val backdropPath: String?
    ) : SearchResultItem()

    data class TvShowItem(
        val tvShow: TvShow,
        override val overview: String?,
        val firstAirDate: String?,
        override val voteAverage: Double?,
        override val voteCount: Int?,
        override val backdropPath: String?
    ) : SearchResultItem()
}

enum class SearchFilter { ALL, MOVIES, TV_SHOWS }