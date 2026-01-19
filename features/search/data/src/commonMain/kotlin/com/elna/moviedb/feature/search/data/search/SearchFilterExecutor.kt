package com.elna.moviedb.feature.search.data.search

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchResultItem
import com.elna.moviedb.core.model.map
import com.elna.moviedb.core.network.SearchRemoteDataSource
import com.elna.moviedb.core.network.model.search.RemoteMultiSearchItem
import com.elna.moviedb.feature.search.data.mapper.toTmdbPath
import com.elna.moviedb.core.network.model.search.RemoteMultiSearchPage
import com.elna.moviedb.core.network.model.search.RemoteSearchMoviesPage
import com.elna.moviedb.core.network.model.search.RemoteSearchPeoplePage
import com.elna.moviedb.core.network.model.search.RemoteSearchTvShowsPage
import com.elna.moviedb.feature.movies.model.Movie
import com.elna.moviedb.feature.search.data.mapper.toSearchResult
import com.elna.moviedb.feature.tvshows.model.TvShow

/**
 * Strategy interface for executing search based on filter type.
 * Each SearchFilter has an associated strategy that knows:
 * - Which response type to request
 * - How to map the response to SearchResultItem list
 */
private fun interface SearchStrategy {
    suspend fun execute(
        searchRemoteDataSource: SearchRemoteDataSource,
        filter: SearchFilter,
        query: String,
        page: Int,
        language: String
    ): AppResult<List<SearchResultItem>>
}

/**
 * Registry mapping SearchFilter to its execution strategy.
 *
 * Adding new filters requires only adding to this registry.
 * The repository code remains unchanged.
 *
 * Benefits:
 * - Centralized filter-to-strategy mapping
 * - Type-safe search execution with reified generics
 * - No modification to repository when adding filters
 */
private val searchStrategies: Map<SearchFilter, SearchStrategy> = mapOf(
    SearchFilter.ALL to SearchStrategy { dataSource, filter, query, page, language ->
        dataSource
            .search<RemoteMultiSearchPage>(filter.toTmdbPath(), query, page, language)
            .map { it.results.mapNotNull { item -> item.toSearchResult() } }
    },

    SearchFilter.MOVIES to SearchStrategy { dataSource, filter, query, page, language ->
        dataSource
            .search<RemoteSearchMoviesPage>(filter.toTmdbPath(), query, page, language)
            .map { it.results.map { item -> item.toSearchResult() } }
    },

    SearchFilter.TV_SHOWS to SearchStrategy { dataSource, filter, query, page, language ->
        dataSource
            .search<RemoteSearchTvShowsPage>(filter.toTmdbPath(), query, page, language)
            .map { it.results.map { item -> item.toSearchResult() } }
    },

    SearchFilter.PEOPLE to SearchStrategy { dataSource, filter, query, page, language ->
        dataSource
            .search<RemoteSearchPeoplePage>(filter.toTmdbPath(), query, page, language)
            .map { it.results.map { item -> item.toSearchResult() } }
    }
)

/**
 * Extension function to execute search using the filter's associated strategy.
 * Looks up the strategy from the registry and delegates execution.
 *
 * @throws IllegalStateException if no strategy is registered for this filter
 */
suspend fun SearchFilter.executeSearch(
    searchRemoteDataSource: SearchRemoteDataSource,
    query: String,
    page: Int,
    language: String
): AppResult<List<SearchResultItem>> {
    val strategy = searchStrategies[this]
        ?: error("No search strategy registered for filter: $this")

    return strategy.execute(searchRemoteDataSource, this, query, page, language)
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
