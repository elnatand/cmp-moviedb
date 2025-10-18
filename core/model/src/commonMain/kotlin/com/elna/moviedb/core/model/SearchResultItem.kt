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
 * Search filter enum following Open/Closed Principle.
 *
 * Each filter has an associated API endpoint path. Adding new filters
 * requires only adding a new enum value with its path - no code changes needed elsewhere.
 *
 * @property apiPath The TMDB API search endpoint path
 */
enum class SearchFilter(val apiPath: String) {
    ALL("search/multi"),
    MOVIES("search/movie"),
    TV_SHOWS("search/tv"),
    PEOPLE("search/person")
}