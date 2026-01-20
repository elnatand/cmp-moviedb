package com.elna.moviedb.feature.search.data.mappers

import com.elna.moviedb.feature.search.data.model.RemoteMultiSearchItem
import com.elna.moviedb.feature.search.data.model.RemoteSearchMovie
import com.elna.moviedb.feature.search.data.model.RemoteSearchPerson
import com.elna.moviedb.feature.search.data.model.RemoteSearchTvShow
import com.elna.moviedb.feature.movies.model.Movie
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchResultItem
import com.elna.moviedb.feature.tvshows.domain.model.TvShow

/**
 * Maps domain SearchFilter to TMDB API endpoint paths.
 *
 * Following Clean Architecture - keeps domain layer independent of infrastructure.
 * This mapper lives in the network layer where TMDB-specific details belong.
 *
 * @return TMDB API search endpoint path (e.g., "search/multi", "search/movie")
 */
fun SearchFilter.toTmdbPath(): String = when (this) {
    SearchFilter.ALL -> "/search/multi"
    SearchFilter.MOVIES -> "/search/movie"
    SearchFilter.TV_SHOWS -> "/search/tv"
    SearchFilter.PEOPLE -> "/search/person"
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


fun RemoteSearchPerson.toSearchResult(): SearchResultItem.PersonItem {
    return SearchResultItem.PersonItem(
        id = id,
        name = name,
        knownForDepartment = knownForDepartment,
        profilePath = profilePath
    )
}

fun RemoteSearchTvShow.toSearchResult(): SearchResultItem.TvShowItem {
    return SearchResultItem.TvShowItem(
        tvShow = TvShow(
            id = id,
            name = name,
            posterPath = posterPath
        ),
        overview = overview,
        firstAirDate = firstAirDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        backdropPath = backdropPath
    )
}


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


