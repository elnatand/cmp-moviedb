package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchFilter
import com.elna.moviedb.core.network.mapper.toTmdbPath
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

/**
 * Remote data source for TMDB search API endpoints.
 *
 * Following Open/Closed Principle - uses SearchFilter enum and mapper pattern.
 * Adding new search filters requires only:
 * 1. Adding to SearchFilter enum (domain)
 * 2. Adding mapping in SearchFilterMapper (network)
 * 3. No changes needed in this class!
 *
 * Uses reified generics to maintain type safety while eliminating method duplication.
 */
class SearchRemoteDataSource(
    @PublishedApi
    internal val httpClient: HttpClient,
    @PublishedApi
    internal val appDispatchers: AppDispatchers
) {

    /**
     * Unified search method using SearchFilter enum.
     * Following Open/Closed Principle - new filters don't require code changes here.
     *
     * @param filter Type-safe search filter (ALL, MOVIES, TV_SHOWS, PEOPLE)
     * @param query The search query string
     * @param page The page number for pagination
     * @param language The language code for results
     * @return AppResult containing the typed search results page
     *
     * Example usage:
     * ```kotlin
     * val result = search<RemoteSearchMoviesPage>(
     *     filter = SearchFilter.MOVIES,
     *     query = "Inception",
     *     page = 1,
     *     language = "en-US"
     * )
     * ```
     */
    suspend inline fun <reified T> search(
        filter: SearchFilter,
        query: String,
        page: Int,
        language: String
    ): AppResult<T> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                // Use mapper to convert domain enum to TMDB API path
                val endpoint = filter.toTmdbPath()
                httpClient.get("$TMDB_BASE_URL$endpoint") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("query", query)
                        parameters.append("page", page.toString())
                        parameters.append("language", language)
                        parameters.append("include_adult", "false")
                    }
                }.body<T>()
            }
        }
    }
}
