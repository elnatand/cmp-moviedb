package com.elna.moviedb.feature.search.data.mapper

import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.network.model.search.RemoteSearchMovie
import com.elna.moviedb.core.network.model.search.RemoteSearchPerson
import com.elna.moviedb.core.network.model.search.RemoteSearchTvShow
import com.elna.moviedb.feature.search.domain.model.SearchResultItem
import com.elna.moviedb.feature.tvshows.model.TvShow

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

fun RemoteSearchTvShow.toDomain(): TvShow {
    return TvShow(
        id = id,
        name = name,
        posterPath = posterPath
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
