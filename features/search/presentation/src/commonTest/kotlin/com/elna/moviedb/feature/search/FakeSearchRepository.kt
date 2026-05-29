package com.elna.moviedb.feature.search

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchPage
import com.elna.moviedb.feature.search.domain.model.SearchResultItem
import com.elna.moviedb.feature.search.domain.repository.SearchRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay

/**
 * Fake [SearchRepository] for testing.
 *
 * Records every [search] invocation and returns a configurable result. An optional
 * delay keeps a call "in flight" so tests can exercise debounce and job-cancellation
 * behavior in [com.elna.moviedb.feature.search.presentation.ui.SearchViewModel].
 */
class FakeSearchRepository : SearchRepository {

    data class Call(val filter: SearchFilter, val query: String, val page: Int)

    val calls = mutableListOf<Call>()

    /** Suspension applied inside [search] after the call is recorded. */
    var searchDelay = 0L

    /** Signals that [search] was entered — useful to await before advancing virtual time. */
    var entered: CompletableDeferred<Unit>? = null

    private var result: AppResult<SearchPage> = AppResult.Success(
        SearchPage(items = emptyList(), page = 1, totalPages = 1)
    )

    fun setResult(result: AppResult<SearchPage>) {
        this.result = result
    }

    fun setSuccess(
        items: List<SearchResultItem>,
        page: Int = 1,
        totalPages: Int = 1
    ) {
        result = AppResult.Success(SearchPage(items = items, page = page, totalPages = totalPages))
    }

    override suspend fun search(
        filter: SearchFilter,
        query: String,
        page: Int
    ): AppResult<SearchPage> {
        calls.add(Call(filter, query, page))
        entered?.complete(Unit)
        if (searchDelay > 0) {
            delay(searchDelay)
        }
        return result
    }
}
