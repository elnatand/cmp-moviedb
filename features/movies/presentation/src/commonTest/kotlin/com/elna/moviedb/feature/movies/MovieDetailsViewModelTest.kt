package com.elna.moviedb.feature.movies

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.DataError
import com.elna.moviedb.feature.movies.model.MovieDetails
import com.elna.moviedb.feature.movies.model.MovieDetailsEvent
import com.elna.moviedb.feature.movies.model.MovieDetailsUiState
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsViewModel
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {

    private lateinit var fakeRepository: FakeMoviesRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMoviesRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `success result is exposed as Success state`() = runTest(testDispatcher) {
        fakeRepository.detailsResult = AppResult.Success(details(id = 11, title = "Inception"))

        val viewModel = MovieDetailsViewModel(movieId = 11, moviesRepository = fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieDetailsUiState.Success)
        assertEquals("Inception", state.movieDetails.title)
    }

    @Test
    fun `error result is exposed as Error state with the error type`() = runTest(testDispatcher) {
        fakeRepository.detailsResult = AppResult.Error("boom", type = DataError.SERVER)

        val viewModel = MovieDetailsViewModel(movieId = 1, moviesRepository = fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieDetailsUiState.Error)
        assertEquals(DataError.SERVER, state.error)
    }

    @Test
    fun `retry re-fetches and recovers from error to success`() = runTest(testDispatcher) {
        fakeRepository.detailsResult = AppResult.Error("boom", type = DataError.NETWORK)
        val viewModel = MovieDetailsViewModel(movieId = 4, moviesRepository = fakeRepository)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is MovieDetailsUiState.Error)

        fakeRepository.detailsResult = AppResult.Success(details(id = 4, title = "Recovered"))
        viewModel.onEvent(MovieDetailsEvent.Retry)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is MovieDetailsUiState.Success)
    }

    @Test
    fun `rapid retry cancels the in-flight load so the latest result wins`() =
        runTest(testDispatcher) {
            fakeRepository.detailsDelay = 1_000
            fakeRepository.detailsResult = AppResult.Error("stale", type = DataError.SERVER)
            val viewModel = MovieDetailsViewModel(movieId = 8, moviesRepository = fakeRepository)
            advanceTimeBy(100) // first load still in flight

            fakeRepository.detailsDelay = 0
            fakeRepository.detailsResult = AppResult.Success(details(id = 8, title = "Winner"))
            viewModel.onEvent(MovieDetailsEvent.Retry)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is MovieDetailsUiState.Success)
            assertEquals("Winner", state.movieDetails.title)
        }

    private fun details(id: Int, title: String) = MovieDetails(
        id = id,
        title = title,
        overview = "",
        posterPath = null,
        backdropPath = null,
        releaseDate = null,
        runtime = null,
        voteAverage = null,
        voteCount = null,
        adult = null,
        budget = null,
        revenue = null,
        homepage = null,
        imdbId = null,
        originalLanguage = null,
        originalTitle = null,
        popularity = null,
        status = null,
        tagline = null,
        genres = null,
        productionCompanies = null,
        productionCountries = null,
        spokenLanguages = null,
    )
}
