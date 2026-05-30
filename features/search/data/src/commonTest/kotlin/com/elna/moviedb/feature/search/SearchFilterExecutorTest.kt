package com.elna.moviedb.feature.search

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.TmdbApiClient
import com.elna.moviedb.feature.search.data.datasources.SearchRemoteDataSource
import com.elna.moviedb.feature.search.data.repositories.executeSearch
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchResultItem
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * End-to-end coverage of the search strategy registry: drives the real
 * [SearchRemoteDataSource] → [TmdbApiClient] → Ktor stack against a [MockEngine], so the
 * filter→endpoint mapping, reified response type dispatch, JSON deserialization, and
 * domain mapping are all exercised together.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchFilterExecutorTest {

    private val requestedPaths = mutableListOf<String>()

    private fun dataSource(json: String): SearchRemoteDataSource {
        requestedPaths.clear()
        val engine = MockEngine { request ->
            requestedPaths += request.url.encodedPath
            respond(
                content = ByteReadChannel(json),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { isLenient = true; ignoreUnknownKeys = true })
            }
        }
        return SearchRemoteDataSource(TmdbApiClient(client, AppDispatchers))
    }

    @Test
    fun `MOVIES filter hits the movie endpoint and maps to MovieItem`() = runTest {
        val body = """
            {
              "page": 1,
              "total_pages": 3,
              "total_results": 1,
              "results": [
                { "id": 27205, "title": "Inception", "original_title": "Inception",
                  "poster_path": "/p.jpg", "backdrop_path": null, "overview": "dreams",
                  "release_date": "2010-07-16", "vote_average": 8.4, "vote_count": 100,
                  "popularity": 1.0, "adult": false, "genre_ids": [28], "original_language": "en",
                  "video": false }
              ]
            }
        """.trimIndent()

        val result = SearchFilter.MOVIES.executeSearch(dataSource(body), "inception", 1, "en-US")

        assertTrue(result is AppResult.Success)
        assertTrue(requestedPaths.single().endsWith("/search/movie"))
        assertEquals(1, result.data.page)
        assertEquals(3, result.data.totalPages)
        val item = result.data.items.single()
        assertTrue(item is SearchResultItem.MovieItem)
        assertEquals("Inception", item.title)
    }

    @Test
    fun `ALL filter hits the multi endpoint and drops unmappable rows`() = runTest {
        // Three rows: a movie, a tv show, and an unsupported "collection" type that the
        // mapper returns null for — it must be filtered out (mapNotNull in the strategy).
        val body = """
            {
              "page": 1,
              "total_pages": 1,
              "total_results": 3,
              "results": [
                { "id": 1, "media_type": "movie", "title": "Inception" },
                { "id": 2, "media_type": "tv", "name": "Severance" },
                { "id": 3, "media_type": "collection", "name": "MCU Collection" }
              ]
            }
        """.trimIndent()

        val result = SearchFilter.ALL.executeSearch(dataSource(body), "q", 1, "en-US")

        assertTrue(result is AppResult.Success)
        assertTrue(requestedPaths.single().endsWith("/search/multi"))
        val items = result.data.items
        assertEquals(2, items.size)
        assertTrue(items[0] is SearchResultItem.MovieItem)
        assertTrue(items[1] is SearchResultItem.TvShowItem)
    }

    @Test
    fun `a server error is surfaced as an AppResult Error`() = runTest {
        val engine = MockEngine { respond(ByteReadChannel(""), HttpStatusCode.InternalServerError) }
        val client = HttpClient(engine) {
            // Mirror production: throw on non-2xx so safeApiCall can classify the failure.
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { isLenient = true; ignoreUnknownKeys = true })
            }
        }
        val dataSource = SearchRemoteDataSource(TmdbApiClient(client, AppDispatchers))

        val result = SearchFilter.MOVIES.executeSearch(dataSource, "q", 1, "en-US")

        assertTrue(result is AppResult.Error)
    }
}
