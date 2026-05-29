package com.elna.moviedb.feature.search

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.DataError
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchResultItem
import com.elna.moviedb.feature.search.presentation.model.SearchEvent
import com.elna.moviedb.feature.search.presentation.ui.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var fakeRepository: FakeSearchRepository
    private lateinit var viewModel: SearchViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    // Debounce window in SearchViewModel.init; queries shorter-lived than this never fire.
    private val debounceMillis = 300L

    private fun movie(id: Int) = SearchResultItem.MovieItem(
        id = id,
        title = "Movie $id",
        posterPath = null,
        overview = null,
        releaseDate = null,
        voteAverage = null,
        voteCount = null,
        backdropPath = null
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeSearchRepository()
        viewModel = SearchViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `blank query never triggers a search`() = runTest(testDispatcher) {
        viewModel.onEvent(SearchEvent.UpdateSearchQuery("   "))
        advanceUntilIdle()

        assertTrue(fakeRepository.calls.isEmpty())
    }

    @Test
    fun `query triggers a search for page 1 after the debounce`() = runTest(testDispatcher) {
        fakeRepository.setSuccess(items = listOf(movie(1), movie(2)))

        viewModel.onEvent(SearchEvent.UpdateSearchQuery("batman"))
        advanceUntilIdle()

        assertEquals(1, fakeRepository.calls.size)
        assertEquals(
            FakeSearchRepository.Call(SearchFilter.ALL, "batman", 1),
            fakeRepository.calls.single()
        )

        val state = viewModel.uiState.value
        assertEquals(2, state.searchResults.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertTrue(state.hasSearched)
    }

    @Test
    fun `rapid keystrokes within the debounce window collapse to a single search`() =
        runTest(testDispatcher) {
            viewModel.onEvent(SearchEvent.UpdateSearchQuery("b"))
            advanceTimeBy(100)
            viewModel.onEvent(SearchEvent.UpdateSearchQuery("ba"))
            advanceTimeBy(100)
            viewModel.onEvent(SearchEvent.UpdateSearchQuery("bat"))
            advanceUntilIdle()

            assertEquals(1, fakeRepository.calls.size)
            assertEquals("bat", fakeRepository.calls.single().query)
        }

    @Test
    fun `an error result is surfaced in state - not as crash or stale results`() =
        runTest(testDispatcher) {
            fakeRepository.setResult(AppResult.Error("boom", type = DataError.NETWORK))

            viewModel.onEvent(SearchEvent.UpdateSearchQuery("batman"))
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(DataError.NETWORK, state.error)
            assertFalse(state.isLoading)
            assertTrue(state.searchResults.isEmpty())
        }

    @Test
    fun `load more appends the next page`() = runTest(testDispatcher) {
        fakeRepository.setSuccess(items = listOf(movie(1)), page = 1, totalPages = 3)
        viewModel.onEvent(SearchEvent.UpdateSearchQuery("batman"))
        advanceUntilIdle()

        fakeRepository.setSuccess(items = listOf(movie(2)), page = 2, totalPages = 3)
        viewModel.onEvent(SearchEvent.LoadMore)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf(1, 2), state.searchResults.map { (it as SearchResultItem.MovieItem).id })
        assertEquals(2, state.currentPage)
        assertTrue(state.hasMorePages)
        assertEquals(2, fakeRepository.calls.last().page)
    }

    @Test
    fun `load more is a no-op when there are no more pages`() = runTest(testDispatcher) {
        fakeRepository.setSuccess(items = listOf(movie(1)), page = 1, totalPages = 1)
        viewModel.onEvent(SearchEvent.UpdateSearchQuery("batman"))
        advanceUntilIdle()
        val callsAfterInitial = fakeRepository.calls.size

        viewModel.onEvent(SearchEvent.LoadMore)
        advanceUntilIdle()

        assertEquals(callsAfterInitial, fakeRepository.calls.size)
        assertFalse(viewModel.uiState.value.hasMorePages)
    }

    @Test
    fun `changing the query resets results and searches again`() = runTest(testDispatcher) {
        fakeRepository.setSuccess(items = listOf(movie(1), movie(2)))
        viewModel.onEvent(SearchEvent.UpdateSearchQuery("batman"))
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.searchResults.size)

        fakeRepository.setSuccess(items = listOf(movie(9)))
        viewModel.onEvent(SearchEvent.UpdateSearchQuery("superman"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.searchResults.size)
        assertEquals("superman", fakeRepository.calls.last().query)
        assertEquals(1, fakeRepository.calls.last().page)
    }

    @Test
    fun `superseding an in-flight search does not leave a stale error or loading state`() =
        runTest(testDispatcher) {
            // First search is kept in flight; the new query must cancel it cleanly
            // (relying on safeApiCall rethrowing CancellationException in production).
            fakeRepository.searchDelay = 1_000
            fakeRepository.setResult(AppResult.Error("stale", type = DataError.SERVER))

            viewModel.onEvent(SearchEvent.UpdateSearchQuery("bat"))
            advanceTimeBy(debounceMillis + 1) // let the first search start
            advanceTimeBy(100)                 // still in flight (delay = 1000)

            // New query supersedes it before it can complete
            fakeRepository.searchDelay = 0
            fakeRepository.setSuccess(items = listOf(movie(7)))
            viewModel.onEvent(SearchEvent.UpdateSearchQuery("superman"))
            advanceUntilIdle()

            val state = viewModel.uiState.value
            // The cancelled (error) search must not have written its state.
            assertNull(state.error)
            assertFalse(state.isLoading)
            assertEquals(listOf(7), state.searchResults.map { (it as SearchResultItem.MovieItem).id })
        }

    @Test
    fun `changing the filter re-runs the search for the new filter`() = runTest(testDispatcher) {
        fakeRepository.setSuccess(items = listOf(movie(1)))
        viewModel.onEvent(SearchEvent.UpdateSearchQuery("batman"))
        advanceUntilIdle()

        viewModel.onEvent(SearchEvent.UpdateFilter(SearchFilter.MOVIES))
        advanceUntilIdle()

        assertEquals(SearchFilter.MOVIES, fakeRepository.calls.last().filter)
        assertEquals(SearchFilter.MOVIES, viewModel.uiState.value.selectedFilter)
    }
}
