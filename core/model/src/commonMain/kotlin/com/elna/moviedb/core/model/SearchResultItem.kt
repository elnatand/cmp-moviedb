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

//    data class TvShowItem(
//    //    val tvShow: TvShow,
//        override val overview: String?,
//        val firstAirDate: String?,
//        override val voteAverage: Double?,
//        override val voteCount: Int?,
//        override val backdropPath: String?
//    ) : SearchResultItem()

    data class PersonItem(
        val id: Int,
        val name: String,
        val knownForDepartment: String?,
        val profilePath: String?,
        override val overview: String? = null,
        override val voteAverage: Double? = null,
        override val voteCount: Int? = null,
        override val backdropPath: String? = null
    ) : SearchResultItem()
}

/**
 * Search filter enum representing domain-level search categories.
 *
 * This is a pure domain model with no infrastructure dependencies.
 * Following Clean Architecture - keeps domain independent of API details.
 *
 * Mapping to infrastructure (e.g., TMDB API paths) is handled in the network layer.
 */
enum class SearchFilter {
    ALL,
    MOVIES,
    TV_SHOWS,
    PEOPLE
}