package com.elna.moviedb.feature.movies

import com.elna.moviedb.core.datastore.pagination.PaginationPreferences
import com.elna.moviedb.core.datastore.pagination.PaginationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory [PaginationPreferences] for tests. */
class FakePaginationPreferences : PaginationPreferences {

    private val states = mutableMapOf<String, MutableStateFlow<PaginationState>>()
    var clearAllCount = 0
        private set

    private fun flowFor(category: String) =
        states.getOrPut(category) { MutableStateFlow(PaginationState()) }

    fun setState(category: String, state: PaginationState) {
        flowFor(category).value = state
    }

    override fun getPaginationState(category: String): Flow<PaginationState> = flowFor(category)

    override suspend fun savePaginationState(category: String, state: PaginationState) {
        flowFor(category).value = state
    }

    override suspend fun clearAllPaginationState() {
        clearAllCount++
        states.values.forEach { it.value = PaginationState() }
    }
}
