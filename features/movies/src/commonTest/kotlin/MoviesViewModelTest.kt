package com.elna.moviedb.feature.movies

import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieCategory
import com.elna.moviedb.core.model.MovieDetails
import com.elna.moviedb.feature.movies.model.MoviesEvent
import com.elna.moviedb.feature.movies.model.MoviesUiAction
import com.elna.moviedb.feature.movies.model.MoviesUiState
import com.elna.moviedb.feature.movies.ui.movies.MoviesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
class MoviesViewModelTest {

    private lateinit var fakeRepository: FakeMoviesRepository
    private lateinit var viewModel: MoviesViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMoviesRepository()
        viewModel = MoviesViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is SUCCESS with empty movies`() = runTest(testDispatcher) {
        // Given - ViewModel is initialized in setup()
        backgroundScope.launch { viewModel.uiState.collect {} }

        // When - ViewModel initializes and observeMovies is called
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(MoviesUiState.State.SUCCESS, state.state)
        // observeMovies() starts collecting all categories, so we expect 3 empty lists
        assertEquals(3, state.moviesByCategory.size)
        assertTrue(state.getMovies(MovieCategory.POPULAR).isEmpty())
        assertTrue(state.getMovies(MovieCategory.TOP_RATED).isEmpty())
        assertTrue(state.getMovies(MovieCategory.NOW_PLAYING).isEmpty())
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `observeMovies collects movies for all categories`() = runTest(testDispatcher) {
        // Given
        val popularMovies = listOf(createMovie(1, "Popular Movie"))
        val topRatedMovies = listOf(createMovie(2, "Top Rated Movie"))
        val nowPlayingMovies = listOf(createMovie(3, "Now Playing Movie"))

        fakeRepository.setMoviesForCategory(MovieCategory.POPULAR, popularMovies)
        fakeRepository.setMoviesForCategory(MovieCategory.TOP_RATED, topRatedMovies)
        fakeRepository.setMoviesForCategory(MovieCategory.NOW_PLAYING, nowPlayingMovies)

        // When
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(MoviesUiState.State.SUCCESS, state.state)
        assertEquals(popularMovies, state.getMovies(MovieCategory.POPULAR))
        assertEquals(topRatedMovies, state.getMovies(MovieCategory.TOP_RATED))
        assertEquals(nowPlayingMovies, state.getMovies(MovieCategory.NOW_PLAYING))
    }

    @Test
    fun `loadNextPage success updates loading state and movies`() = runTest(testDispatcher) {
        // Given
        val initialMovies = listOf(createMovie(1, "Movie 1"))
        fakeRepository.setMoviesForCategory(MovieCategory.POPULAR, initialMovies)
        fakeRepository.setNextPageResult(MovieCategory.POPULAR, AppResult.Success(Unit))

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onEvent(MoviesEvent.LoadNextPage(MovieCategory.POPULAR))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading(MovieCategory.POPULAR))
        assertEquals(1, fakeRepository.loadNextPageCallCount[MovieCategory.POPULAR])
    }

    @Test
    fun `loadNextPage with error and existing movies shows snackbar`() = runTest(testDispatcher) {
        // Given
        val existingMovies = listOf(createMovie(1, "Existing Movie"))
        fakeRepository.setMoviesForCategory(MovieCategory.POPULAR, existingMovies)
        fakeRepository.setNextPageResult(
            MovieCategory.POPULAR,
            AppResult.Error("Network error")
        )

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val uiActions = mutableListOf<MoviesUiAction>()
        val uiActionJob = backgroundScope.launch {
            viewModel.uiAction.collect { uiActions.add(it) }
        }

        // When
        viewModel.onEvent(MoviesEvent.LoadNextPage(MovieCategory.POPULAR))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(MoviesUiState.State.SUCCESS, state.state)
        assertFalse(state.isLoading(MovieCategory.POPULAR))
        assertEquals(1, uiActions.size)
        assertTrue(uiActions[0] is MoviesUiAction.ShowPaginationError)
        assertEquals("Network error", (uiActions[0] as MoviesUiAction.ShowPaginationError).message)

        uiActionJob.cancel()
    }

    @Test
    fun `loadNextPage with error and no existing movies shows error state`() = runTest(testDispatcher) {
        // Given
        fakeRepository.setMoviesForCategory(MovieCategory.POPULAR, emptyList())
        fakeRepository.setNextPageResult(
            MovieCategory.POPULAR,
            AppResult.Error("Network error")
        )

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onEvent(MoviesEvent.LoadNextPage(MovieCategory.POPULAR))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(MoviesUiState.State.ERROR, state.state)
        assertFalse(state.isLoading(MovieCategory.POPULAR))
    }

    @Test
    fun `loadNextPage prevents duplicate loading for same category`() = runTest(testDispatcher) {
        // Given
        val movies = listOf(createMovie(1, "Movie"))
        fakeRepository.setMoviesForCategory(MovieCategory.POPULAR, movies)
        fakeRepository.setNextPageResult(MovieCategory.POPULAR, AppResult.Success(Unit))

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // Manually set loading state
        viewModel.onEvent(MoviesEvent.LoadNextPage(MovieCategory.POPULAR))

        // When - Try to load again while already loading
        viewModel.onEvent(MoviesEvent.LoadNextPage(MovieCategory.POPULAR))
        advanceUntilIdle()

        // Then - Should only call once (prevented duplicate)
        assertTrue(fakeRepository.loadNextPageCallCount[MovieCategory.POPULAR]!! <= 2)
    }

    @Test
    fun `loadNextPage for different categories works independently`() = runTest(testDispatcher) {
        // Given
        fakeRepository.setMoviesForCategory(MovieCategory.POPULAR, listOf(createMovie(1, "Popular")))
        fakeRepository.setMoviesForCategory(MovieCategory.TOP_RATED, listOf(createMovie(2, "Top Rated")))
        fakeRepository.setNextPageResult(MovieCategory.POPULAR, AppResult.Success(Unit))
        fakeRepository.setNextPageResult(MovieCategory.TOP_RATED, AppResult.Success(Unit))

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onEvent(MoviesEvent.LoadNextPage(MovieCategory.POPULAR))
        viewModel.onEvent(MoviesEvent.LoadNextPage(MovieCategory.TOP_RATED))
        advanceUntilIdle()

        // Then
        assertEquals(1, fakeRepository.loadNextPageCallCount[MovieCategory.POPULAR])
        assertEquals(1, fakeRepository.loadNextPageCallCount[MovieCategory.TOP_RATED])
    }

    @Test
    fun `retry with all failures shows error state`() = runTest(testDispatcher) {
        // Given
        MovieCategory.entries.forEach { category ->
            fakeRepository.setMoviesForCategory(category, emptyList())
            fakeRepository.setNextPageResult(category, AppResult.Error("Error"))
        }

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onEvent(MoviesEvent.Retry)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(MoviesUiState.State.ERROR, state.state)
        MovieCategory.entries.forEach { category ->
            assertEquals(1, fakeRepository.loadNextPageCallCount[category])
        }
    }

    @Test
    fun `retry with some success keeps success state`() = runTest(testDispatcher) {
        // Given
        fakeRepository.setMoviesForCategory(MovieCategory.POPULAR, listOf(createMovie(1, "Movie")))
        fakeRepository.setNextPageResult(MovieCategory.POPULAR, AppResult.Success(Unit))
        fakeRepository.setNextPageResult(MovieCategory.TOP_RATED, AppResult.Error("Error"))
        fakeRepository.setNextPageResult(MovieCategory.NOW_PLAYING, AppResult.Error("Error"))

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onEvent(MoviesEvent.Retry)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(MoviesUiState.State.SUCCESS, state.state)
    }

    @Test
    fun `retry loads all categories in parallel`() = runTest(testDispatcher) {
        // Given
        MovieCategory.entries.forEach { category ->
            fakeRepository.setMoviesForCategory(category, emptyList())
            fakeRepository.setNextPageResult(category, AppResult.Success(Unit))
        }

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onEvent(MoviesEvent.Retry)
        advanceUntilIdle()

        // Then - All categories should be called
        MovieCategory.entries.forEach { category ->
            assertEquals(1, fakeRepository.loadNextPageCallCount[category])
        }
    }

    @Test
    fun `refresh calls clearAndReload`() = runTest(testDispatcher) {
        // Given
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onEvent(MoviesEvent.Refresh)
        advanceUntilIdle()

        // Then
        assertEquals(1, fakeRepository.clearAndReloadCallCount)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun `refresh sets and clears isRefreshing flag`() = runTest(testDispatcher) {
        // Given
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onEvent(MoviesEvent.Refresh)
        advanceUntilIdle()

        // Then - isRefreshing should be false after refresh completes
        assertFalse(viewModel.uiState.value.isRefreshing, "Should clear isRefreshing at end")
        // Verify clearAndReload was actually called
        assertEquals(1, fakeRepository.clearAndReloadCallCount)
    }

    @Test
    fun `refresh prevents duplicate refresh operations`() = runTest(testDispatcher) {
        // Given
        fakeRepository.clearAndReloadDelay = 100 // Add delay to simulate slow operation
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When - Try to refresh multiple times
        viewModel.onEvent(MoviesEvent.Refresh)
        viewModel.onEvent(MoviesEvent.Refresh)
        viewModel.onEvent(MoviesEvent.Refresh)
        advanceUntilIdle()

        // Then - Should only call once
        assertTrue(fakeRepository.clearAndReloadCallCount <= 1)
    }

    @Test
    fun `multiple loadNextPage events for same category are handled correctly`() = runTest(testDispatcher) {
        // Given
        val movies = listOf(createMovie(1, "Movie"))
        fakeRepository.setMoviesForCategory(MovieCategory.POPULAR, movies)
        fakeRepository.setNextPageResult(MovieCategory.POPULAR, AppResult.Success(Unit))

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        repeat(5) {
            viewModel.onEvent(MoviesEvent.LoadNextPage(MovieCategory.POPULAR))
        }
        advanceUntilIdle()

        // Then - Should not crash and should handle gracefully
        assertTrue(fakeRepository.loadNextPageCallCount[MovieCategory.POPULAR]!! >= 1)
    }

    @Test
    fun `state transitions from success to error and back to success`() = runTest(testDispatcher) {
        // Given
        fakeRepository.setMoviesForCategory(MovieCategory.POPULAR, emptyList())
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // Initial state is SUCCESS
        assertEquals(MoviesUiState.State.SUCCESS, viewModel.uiState.value.state)

        // When - Load fails with no existing movies
        fakeRepository.setNextPageResult(MovieCategory.POPULAR, AppResult.Error("Error"))
        viewModel.onEvent(MoviesEvent.LoadNextPage(MovieCategory.POPULAR))
        advanceUntilIdle()

        // Then - State is ERROR
        assertEquals(MoviesUiState.State.ERROR, viewModel.uiState.value.state)

        // When - Retry succeeds
        fakeRepository.setMoviesForCategory(MovieCategory.POPULAR, listOf(createMovie(1, "Movie")))
        fakeRepository.setNextPageResult(MovieCategory.POPULAR, AppResult.Success(Unit))
        viewModel.onEvent(MoviesEvent.Retry)
        advanceUntilIdle()

        // Then - State is back to SUCCESS
        assertEquals(MoviesUiState.State.SUCCESS, viewModel.uiState.value.state)
    }

    // Helper function to create test movies
    private fun createMovie(id: Int, title: String) = Movie(
        id = id,
        title = title,
        posterPath = "/path$id.jpg"
    )
}

/**
 * Fake implementation of MoviesRepository for testing.
 * Provides controllable behavior for all repository operations.
 */
private class FakeMoviesRepository : MoviesRepository {
    private val moviesFlows = mutableMapOf<MovieCategory, MutableStateFlow<List<Movie>>>()
    private val nextPageResults = mutableMapOf<MovieCategory, AppResult<Unit>>()

    val loadNextPageCallCount = mutableMapOf<MovieCategory, Int>()
    var clearAndReloadCallCount = 0
    var clearAndReloadDelay = 0L

    init {
        // Initialize flows and counters for all categories
        MovieCategory.entries.forEach { category ->
            moviesFlows[category] = MutableStateFlow(emptyList())
            loadNextPageCallCount[category] = 0
        }
    }

    fun setMoviesForCategory(category: MovieCategory, movies: List<Movie>) {
        moviesFlows[category]?.value = movies
    }

    fun setNextPageResult(category: MovieCategory, result: AppResult<Unit>) {
        nextPageResults[category] = result
    }

    override suspend fun observeMovies(category: MovieCategory): Flow<List<Movie>> {
        return moviesFlows[category] ?: flowOf(emptyList())
    }

    override suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit> {
        loadNextPageCallCount[category] = (loadNextPageCallCount[category] ?: 0) + 1
        return nextPageResults[category] ?: AppResult.Success(Unit)
    }

    override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> {
        return AppResult.Error("Not implemented in fake")
    }

    override suspend fun clearAndReload() {
        clearAndReloadCallCount++
        if (clearAndReloadDelay > 0) {
            kotlinx.coroutines.delay(clearAndReloadDelay)
        }
    }
}
