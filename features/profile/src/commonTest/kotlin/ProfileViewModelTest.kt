package com.elna.moviedb.feature.profile

import com.elna.moviedb.core.common.utils.FakeAppVersion
import com.elna.moviedb.core.datastore.FakeAppSettingsPreferences
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import com.elna.moviedb.feature.profile.model.ProfileEvent
import com.elna.moviedb.feature.profile.ui.ProfileViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var fakeAppSettingsPreferences: FakeAppSettingsPreferences
    private lateinit var fakeAppVersion: FakeAppVersion
    private lateinit var viewModel: ProfileViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeAppSettingsPreferences = FakeAppSettingsPreferences()
        fakeAppVersion = FakeAppVersion()
        viewModel = ProfileViewModel(fakeAppSettingsPreferences, fakeAppVersion)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent with SetLanguage updates language preference`() = runTest {
        // Given
        val newLanguage = AppLanguage.HEBREW

        // When
        viewModel.onEvent(ProfileEvent.SetLanguage(newLanguage))

        // Then
        assertEquals(AppLanguage.HEBREW, fakeAppSettingsPreferences.lastSetLanguage)
    }

    @Test
    fun `onEvent with SetTheme updates theme preference`() = runTest {
        // Given
        val newTheme = AppTheme.DARK

        // When
        viewModel.onEvent(ProfileEvent.SetTheme(newTheme))

        // Then
        assertEquals(AppTheme.DARK, fakeAppSettingsPreferences.lastSetTheme)
    }

    @Test
    fun `onEvent with SetLanguage updates UI state with new language`() = runTest {
        // Given
        val newLanguage = AppLanguage.ARABIC

        // Start collecting the StateFlow to make it active
        val job = launch { viewModel.uiState.collect {} }

        // When
        viewModel.onEvent(ProfileEvent.SetLanguage(newLanguage))
        advanceUntilIdle()

        // Then
        assertEquals(AppLanguage.ARABIC.code, viewModel.uiState.value.selectedLanguageCode)

        job.cancel()
    }

    @Test
    fun `onEvent with SetTheme updates UI state with new theme`() = runTest {
        // Given
        val newTheme = AppTheme.LIGHT

        // Start collecting the StateFlow to make it active
        val job = launch { viewModel.uiState.collect {} }

        // When
        viewModel.onEvent(ProfileEvent.SetTheme(newTheme))
        advanceUntilIdle()

        // Then
        assertEquals(AppTheme.LIGHT.value, viewModel.uiState.value.selectedThemeValue)

        job.cancel()
    }

    @Test
    fun `onEvent with multiple language changes updates state correctly`() = runTest {
        // Given
        val firstLanguage = AppLanguage.HINDI
        val secondLanguage = AppLanguage.HEBREW

        // Start collecting the StateFlow to make it active
        val job = launch { viewModel.uiState.collect {} }

        // When
        viewModel.onEvent(ProfileEvent.SetLanguage(firstLanguage))
        advanceUntilIdle()
        viewModel.onEvent(ProfileEvent.SetLanguage(secondLanguage))
        advanceUntilIdle()

        // Then
        assertEquals(AppLanguage.HEBREW, fakeAppSettingsPreferences.lastSetLanguage)
        assertEquals(AppLanguage.HEBREW.code, viewModel.uiState.value.selectedLanguageCode)

        job.cancel()
    }

    @Test
    fun `onEvent with multiple theme changes updates state correctly`() = runTest {
        // Given
        val firstTheme = AppTheme.LIGHT
        val secondTheme = AppTheme.DARK

        // Start collecting the StateFlow to make it active
        val job = launch { viewModel.uiState.collect {} }

        // When
        viewModel.onEvent(ProfileEvent.SetTheme(firstTheme))
        advanceUntilIdle()
        viewModel.onEvent(ProfileEvent.SetTheme(secondTheme))
        advanceUntilIdle()

        // Then
        assertEquals(AppTheme.DARK, fakeAppSettingsPreferences.lastSetTheme)
        assertEquals(AppTheme.DARK.value, viewModel.uiState.value.selectedThemeValue)

        job.cancel()
    }

    @Test
    fun `initial UI state has correct default values`() = runTest {
        // When - ViewModel is initialized
        val state = viewModel.uiState.value

        // Then
        assertEquals(AppLanguage.ENGLISH.code, state.selectedLanguageCode)
        assertEquals(AppTheme.SYSTEM.value, state.selectedThemeValue)
        assertEquals("1.0.0-test", state.appVersion)
    }
}
