package com.elna.moviedb.feature.tvshows

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.DataError
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowDetailsEvent
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowDetailsUiState
import com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details.TvShowDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
class TvShowDetailsViewModelTest {

    private lateinit var fakeRepository: FakeTvShowsRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTvShowsRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `success result is exposed as Success state`() = runTest(testDispatcher) {
        fakeRepository.detailsResult = AppResult.Success(details(id = 42, name = "Breaking Bad"))

        val viewModel = TvShowDetailsViewModel(tvShowId = 42, tvShowsRepository = fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is TvShowDetailsUiState.Success)
        assertEquals("Breaking Bad", state.tvShowDetails.name)
    }

    @Test
    fun `error result is exposed as Error state with the error type`() = runTest(testDispatcher) {
        fakeRepository.detailsResult = AppResult.Error("boom", type = DataError.SERVER)

        val viewModel = TvShowDetailsViewModel(tvShowId = 1, tvShowsRepository = fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is TvShowDetailsUiState.Error)
        assertEquals(DataError.SERVER, state.error)
    }

    @Test
    fun `retry re-fetches and recovers from error to success`() = runTest(testDispatcher) {
        fakeRepository.detailsResult = AppResult.Error("boom", type = DataError.NETWORK)
        val viewModel = TvShowDetailsViewModel(tvShowId = 7, tvShowsRepository = fakeRepository)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is TvShowDetailsUiState.Error)

        fakeRepository.detailsResult = AppResult.Success(details(id = 7, name = "Recovered"))
        viewModel.onEvent(TvShowDetailsEvent.Retry)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is TvShowDetailsUiState.Success)
        assertEquals("Recovered", state.tvShowDetails.name)
    }

    private fun details(id: Int, name: String) = TvShowDetails(
        id = id,
        name = name,
        overview = null,
        posterPath = null,
        backdropPath = null,
        adult = null,
        firstAirDate = null,
        lastAirDate = null,
        numberOfEpisodes = null,
        numberOfSeasons = null,
        episodeRunTime = null,
        status = null,
        tagline = null,
        type = null,
        voteAverage = null,
        voteCount = null,
        popularity = null,
        originalName = null,
        originalLanguage = null,
        originCountry = null,
        homepage = null,
        inProduction = null,
        languages = null,
        genres = null,
        networks = null,
        productionCompanies = null,
        productionCountries = null,
        spokenLanguages = null,
        seasonsCount = null,
        createdBy = null,
        lastEpisodeName = null,
        lastEpisodeAirDate = null,
        nextEpisodeToAir = null,
        nextEpisodeAirDate = null,
    )
}
