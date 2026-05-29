package com.elna.moviedb.feature.tvshows

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.tvshows.domain.model.TvShow
import com.elna.moviedb.feature.tvshows.domain.model.TvShowCategory
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsEvent
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsUiAction
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsUiState
import com.elna.moviedb.feature.tvshows.presentation.ui.tv_shows.TvShowsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TvShowsViewModelTest {

    private lateinit var fakeRepository: FakeTvShowsRepository
    private lateinit var viewModel: TvShowsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTvShowsRepository()
        viewModel = TvShowsViewModel(fakeRepository)
        // Constructing the ViewModel starts observing and triggers one initial load per
        // (empty) category. Reset counters so each test asserts only its own interactions.
        fakeRepository.resetCounters()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init triggers an initial load for each empty category`() = runTest(testDispatcher) {
        val freshRepository = FakeTvShowsRepository()

        TvShowsViewModel(freshRepository)
        advanceUntilIdle()

        TvShowCategory.entries.forEach { category ->
            assertEquals(1, freshRepository.loadNextPageCallCount[category])
        }
    }

    @Test
    fun `state is SUCCESS with empty shows after initial observe`() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TvShowsUiState.State.SUCCESS, state.state)
        assertEquals(TvShowCategory.entries.size, state.tvShowsByCategory.size)
        TvShowCategory.entries.forEach { assertTrue(state.getTvShows(it).isEmpty()) }
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `observeTvShows collects shows for all categories`() = runTest(testDispatcher) {
        fakeRepository.setTvShowsForCategory(TvShowCategory.POPULAR, listOf(show(1, "Popular")))
        fakeRepository.setTvShowsForCategory(TvShowCategory.ON_THE_AIR, listOf(show(2, "On Air")))
        fakeRepository.setTvShowsForCategory(TvShowCategory.TOP_RATED, listOf(show(3, "Top Rated")))

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TvShowsUiState.State.SUCCESS, state.state)
        assertEquals(listOf(show(1, "Popular")), state.getTvShows(TvShowCategory.POPULAR))
        assertEquals(listOf(show(2, "On Air")), state.getTvShows(TvShowCategory.ON_THE_AIR))
        assertEquals(listOf(show(3, "Top Rated")), state.getTvShows(TvShowCategory.TOP_RATED))
    }

    @Test
    fun `loadNextPage error with existing data shows snackbar and keeps content`() =
        runTest(testDispatcher) {
            fakeRepository.setTvShowsForCategory(TvShowCategory.POPULAR, listOf(show(1, "Existing")))
            fakeRepository.setNextPageResult(TvShowCategory.POPULAR, AppResult.Error("boom"))

            backgroundScope.launch { viewModel.uiState.collect {} }
            advanceUntilIdle()

            val actions = mutableListOf<TvShowsUiAction>()
            val job = backgroundScope.launch { viewModel.uiAction.collect { actions.add(it) } }

            viewModel.onEvent(TvShowsEvent.LoadNextPage(TvShowCategory.POPULAR))
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(TvShowsUiState.State.SUCCESS, state.state)
            assertEquals(1, actions.size)
            assertTrue(actions[0] is TvShowsUiAction.ShowPaginationError)

            job.cancel()
        }

    @Test
    fun `loadNextPage error with no data shows full-screen error`() = runTest(testDispatcher) {
        fakeRepository.setNextPageResult(TvShowCategory.POPULAR, AppResult.Error("boom"))

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onEvent(TvShowsEvent.LoadNextPage(TvShowCategory.POPULAR))
        advanceUntilIdle()

        assertEquals(TvShowsUiState.State.ERROR, viewModel.uiState.value.state)
    }

    @Test
    fun `loadNextPage prevents duplicate concurrent loads for same category`() =
        runTest(testDispatcher) {
            fakeRepository.setTvShowsForCategory(TvShowCategory.POPULAR, listOf(show(1, "Show")))
            fakeRepository.loadNextPageDelay = 100

            backgroundScope.launch { viewModel.uiState.collect {} }
            advanceUntilIdle()

            viewModel.onEvent(TvShowsEvent.LoadNextPage(TvShowCategory.POPULAR))
            viewModel.onEvent(TvShowsEvent.LoadNextPage(TvShowCategory.POPULAR))
            advanceUntilIdle()

            assertEquals(1, fakeRepository.loadNextPageCallCount[TvShowCategory.POPULAR])
        }

    @Test
    fun `retry with all failures shows error state`() = runTest(testDispatcher) {
        TvShowCategory.entries.forEach {
            fakeRepository.setNextPageResult(it, AppResult.Error("boom"))
        }

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onEvent(TvShowsEvent.Retry)
        advanceUntilIdle()

        assertEquals(TvShowsUiState.State.ERROR, viewModel.uiState.value.state)
        TvShowCategory.entries.forEach {
            assertEquals(1, fakeRepository.loadNextPageCallCount[it])
        }
    }

    @Test
    fun `retry with partial success keeps success state`() = runTest(testDispatcher) {
        fakeRepository.setTvShowsForCategory(TvShowCategory.POPULAR, listOf(show(1, "Show")))
        fakeRepository.setNextPageResult(TvShowCategory.POPULAR, AppResult.Success(Unit))
        fakeRepository.setNextPageResult(TvShowCategory.ON_THE_AIR, AppResult.Error("boom"))
        fakeRepository.setNextPageResult(TvShowCategory.TOP_RATED, AppResult.Error("boom"))

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onEvent(TvShowsEvent.Retry)
        advanceUntilIdle()

        assertEquals(TvShowsUiState.State.SUCCESS, viewModel.uiState.value.state)
    }

    @Test
    fun `refresh calls clearAndReload and clears the refreshing flag`() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onEvent(TvShowsEvent.Refresh)
        advanceUntilIdle()

        assertEquals(1, fakeRepository.clearAndReloadCallCount)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun `refresh prevents duplicate concurrent refreshes`() = runTest(testDispatcher) {
        fakeRepository.clearAndReloadDelay = 100

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onEvent(TvShowsEvent.Refresh)
        viewModel.onEvent(TvShowsEvent.Refresh)
        viewModel.onEvent(TvShowsEvent.Refresh)
        advanceUntilIdle()

        assertEquals(1, fakeRepository.clearAndReloadCallCount)
    }

    private fun show(id: Int, name: String) = TvShow(id = id, name = name, posterPath = null)
}
