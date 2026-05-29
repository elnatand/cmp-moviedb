package com.elna.moviedb.feature.person

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.DataError
import com.elna.moviedb.feature.person.domain.model.PersonDetails
import com.elna.moviedb.feature.person.presentation.model.PersonDetailsEvent
import com.elna.moviedb.feature.person.presentation.model.PersonUiState
import com.elna.moviedb.feature.person.presentation.ui.PersonDetailsViewModel
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
class PersonDetailsViewModelTest {

    private lateinit var fakeRepository: FakePersonRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakePersonRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `success result is exposed as Success state`() = runTest(testDispatcher) {
        fakeRepository.result = AppResult.Success(person(id = 5, name = "Bryan Cranston"))

        val viewModel = PersonDetailsViewModel(personId = 5, personRepository = fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PersonUiState.Success)
        assertEquals("Bryan Cranston", state.person.name)
    }

    @Test
    fun `error result is exposed as Error state with the error type`() = runTest(testDispatcher) {
        fakeRepository.result = AppResult.Error("boom", type = DataError.CLIENT)

        val viewModel = PersonDetailsViewModel(personId = 1, personRepository = fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PersonUiState.Error)
        assertEquals(DataError.CLIENT, state.error)
    }

    @Test
    fun `retry re-fetches and recovers from error to success`() = runTest(testDispatcher) {
        fakeRepository.result = AppResult.Error("boom", type = DataError.NETWORK)
        val viewModel = PersonDetailsViewModel(personId = 9, personRepository = fakeRepository)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is PersonUiState.Error)

        fakeRepository.result = AppResult.Success(person(id = 9, name = "Recovered"))
        viewModel.onEvent(PersonDetailsEvent.Retry)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is PersonUiState.Success)
    }

    @Test
    fun `rapid retry cancels the in-flight load so the latest result wins`() =
        runTest(testDispatcher) {
            // First load is slow and would resolve to an error.
            fakeRepository.delayMillis = 1_000
            fakeRepository.result = AppResult.Error("stale", type = DataError.SERVER)
            val viewModel = PersonDetailsViewModel(personId = 3, personRepository = fakeRepository)
            advanceTimeBy(100) // first load still in flight

            // Retry supersedes it with a fast success.
            fakeRepository.delayMillis = 0
            fakeRepository.result = AppResult.Success(person(id = 3, name = "Winner"))
            viewModel.onEvent(PersonDetailsEvent.Retry)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            // The cancelled stale error must not overwrite the latest success.
            assertTrue(state is PersonUiState.Success)
            assertEquals("Winner", state.person.name)
        }

    private fun person(id: Int, name: String) = PersonDetails(
        id = id,
        name = name,
        biography = "",
        birthday = null,
        deathday = null,
        gender = "",
        homepage = null,
        imdbId = null,
        knownForDepartment = "",
        placeOfBirth = null,
        popularity = null,
        profilePath = null,
        adult = false,
        alsoKnownAs = emptyList(),
    )
}
