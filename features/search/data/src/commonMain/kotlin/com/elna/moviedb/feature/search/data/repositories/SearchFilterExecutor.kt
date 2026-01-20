package com.elna.moviedb.feature.search.data.repositories

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.map
import com.elna.moviedb.feature.search.data.datasources.SearchRemoteDataSource
import com.elna.moviedb.feature.search.data.model.RemoteMultiSearchPage
import com.elna.moviedb.feature.search.data.model.RemoteSearchMoviesPage
import com.elna.moviedb.feature.search.data.model.RemoteSearchPeoplePage
import com.elna.moviedb.feature.search.data.model.RemoteSearchTvShowsPage
import com.elna.moviedb.feature.search.data.mappers.toSearchResult
import com.elna.moviedb.feature.search.data.mappers.toTmdbPath
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchResultItem

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
