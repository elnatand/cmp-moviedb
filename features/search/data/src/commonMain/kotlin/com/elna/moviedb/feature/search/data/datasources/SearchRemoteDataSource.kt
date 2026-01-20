package com.elna.moviedb.feature.search.data.datasources

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.TmdbApiClient

/**
 * Remote data source for TMDB search API endpoints.
 *
 * Uses SearchFilter enum and mapper pattern.
 * Adding new search filters requires only:
 * 1. Adding to SearchFilter enum (domain)
 * 2. Adding mapping in SearchFilterMapper (network)
 * 3. No changes needed in this class
 *
 * Uses reified generics to maintain type safety while eliminating method duplication.
 */
class SearchRemoteDataSource(
    @PublishedApi
    internal val apiClient: TmdbApiClient
) {

    /**
     * Unified search method using SearchFilter enum.
     * New filters don't require code changes here.
     *
     * @param endpoint The search endpoint path
     * @param query The search query string
     * @param page The page number for pagination
     * @param language The language code for results
     * @return AppResult containing the typed search results page
     *
     * Example usage:
     * ```kotlin
     * val result = search<RemoteSearchMoviesPage>(
     *     endpoint = "/search/movie",
     *     query = "Inception",
     *     page = 1,
     *     language = "en-US"
     * )
     * ```
     */
    suspend inline fun <reified T> search(
        endpoint: String,
        query: String,
        page: Int,
        language: String
    ): AppResult<T> {
        return apiClient.get(
            path = endpoint,
            "query" to query,
            "page" to page.toString(),
            "language" to language,
            "include_adult" to "false"
        )
    }
}
