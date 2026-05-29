package com.elna.moviedb.core.datastore.language

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.datastore.FakeAppSettingsPreferences
import com.elna.moviedb.core.model.AppLanguage
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

@OptIn(ExperimentalCoroutinesApi::class)
class LanguageChangeCoordinatorTest {

    private lateinit var prefs: FakeAppSettingsPreferences
    private val testDispatcher = UnconfinedTestDispatcher()

    /**
     * A listener that records how many times it was notified, and can optionally throw to
     * verify per-listener failure isolation.
     */
    private class RecordingListener(private val throwOnNotify: Boolean = false) :
        LanguageChangeListener {
        var notifications = 0
            private set

        override suspend fun onLanguageChanged() {
            notifications++
            if (throwOnNotify) throw RuntimeException("listener boom")
        }
    }

    @BeforeTest
    fun setup() {
        // The coordinator confines its work to appDispatchers.main (= Dispatchers.Main).
        Dispatchers.setMain(testDispatcher)
        prefs = FakeAppSettingsPreferences()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun coordinator() = LanguageChangeCoordinator(prefs, AppDispatchers)

    @Test
    fun `initial language emission does not notify listeners`() = runTest(testDispatcher) {
        val coordinator = coordinator()
        val listener = RecordingListener()
        coordinator.registerListener(listener)
        advanceUntilIdle()

        // drop(1) skips the initial value, so no notification on startup.
        assertEquals(0, listener.notifications)
    }

    @Test
    fun `language change notifies registered listeners`() = runTest(testDispatcher) {
        val coordinator = coordinator()
        val listener = RecordingListener()
        coordinator.registerListener(listener)
        advanceUntilIdle()

        prefs.setAppLanguageCode(AppLanguage.HEBREW)
        advanceUntilIdle()

        assertEquals(1, listener.notifications)
    }

    @Test
    fun `repeated identical language is de-duplicated`() = runTest(testDispatcher) {
        val coordinator = coordinator()
        val listener = RecordingListener()
        coordinator.registerListener(listener)
        advanceUntilIdle()

        prefs.setAppLanguageCode(AppLanguage.ARABIC)
        prefs.setAppLanguageCode(AppLanguage.ARABIC)
        advanceUntilIdle()

        // distinctUntilChanged collapses the duplicate.
        assertEquals(1, listener.notifications)
    }

    @Test
    fun `a throwing listener does not prevent other listeners from being notified`() =
        runTest(testDispatcher) {
            val coordinator = coordinator()
            val throwing = RecordingListener(throwOnNotify = true)
            val healthy = RecordingListener()
            coordinator.registerListener(throwing)
            coordinator.registerListener(healthy)
            advanceUntilIdle()

            prefs.setAppLanguageCode(AppLanguage.HINDI)
            advanceUntilIdle()

            assertEquals(1, throwing.notifications)
            assertEquals(1, healthy.notifications)
        }

    @Test
    fun `a throwing listener does not stop future language changes`() = runTest(testDispatcher) {
        val coordinator = coordinator()
        val throwing = RecordingListener(throwOnNotify = true)
        coordinator.registerListener(throwing)
        advanceUntilIdle()

        prefs.setAppLanguageCode(AppLanguage.HINDI)
        advanceUntilIdle()
        prefs.setAppLanguageCode(AppLanguage.HEBREW)
        advanceUntilIdle()

        // The collection coroutine survived the first failure and processed the second change.
        assertEquals(2, throwing.notifications)
    }
}
