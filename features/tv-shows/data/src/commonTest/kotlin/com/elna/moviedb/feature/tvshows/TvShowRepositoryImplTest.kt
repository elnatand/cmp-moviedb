package com.elna.moviedb.feature.tvshows

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.datastore.FakeAppSettingsPreferences
import com.elna.moviedb.core.datastore.language.LanguageChangeCoordinator
import com.elna.moviedb.core.datastore.language.LanguageProvider
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.tvshows.FakeTvShowsRemoteService.Companion.tvShow
import com.elna.moviedb.feature.tvshows.data.model.RemoteTvShowsPage
import com.elna.moviedb.feature.tvshows.data.repositories.TvShowRepositoryImpl
import com.elna.moviedb.feature.tvshows.domain.model.TvShowCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TvShowRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var remote: FakeTvShowsRemoteService
    private lateinit var repository: TvShowRepositoryImpl

    @BeforeTest
    fun setup() {
        // LanguageChangeCoordinator launches a collector on Dispatchers.Main.
        Dispatchers.setMain(testDispatcher)
        remote = FakeTvShowsRemoteService()
        val prefs = FakeAppSettingsPreferences()
        repository = TvShowRepositoryImpl(
            remoteDataSource = remote,
            languageProvider = LanguageProvider(prefs),
            languageChangeCoordinator = LanguageChangeCoordinator(prefs, AppDispatchers),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- loadTvShowsNextPage ---

    @Test
    fun `loadTvShowsNextPage emits fetched shows on the category flow and advances the page`() =
        runTest(testDispatcher) {
            remote.resultByPath["/tv/popular"] = AppResult.Success(
                RemoteTvShowsPage(
                    page = 1,
                    totalPages = 5,
                    results = listOf(tvShow(10, "A"), tvShow(11, "B"))
                )
            )

            val result = repository.loadTvShowsNextPage(TvShowCategory.POPULAR)
            assertTrue(result is AppResult.Success)

            val shows = repository.observeTvShows(TvShowCategory.POPULAR).first()
            assertEquals(listOf(10, 11), shows.map { it.id })
            assertEquals(listOf("/tv/popular" to 1), remote.fetchedPages)
        }

    @Test
    fun `loadTvShowsNextPage de-duplicates shows that repeat across pages by id`() =
        runTest(testDispatcher) {
            // Page 1 returns shows 1 and 2; page 2 repeats show 2 (a common TMDB quirk) plus 3.
            remote.pageByNumber[1] = AppResult.Success(
                RemoteTvShowsPage(page = 1, totalPages = 5, results = listOf(tvShow(1, "A"), tvShow(2, "B")))
            )
            remote.pageByNumber[2] = AppResult.Success(
                RemoteTvShowsPage(page = 2, totalPages = 5, results = listOf(tvShow(2, "B"), tvShow(3, "C")))
            )

            repository.loadTvShowsNextPage(TvShowCategory.POPULAR)
            repository.loadTvShowsNextPage(TvShowCategory.POPULAR)

            val shows = repository.observeTvShows(TvShowCategory.POPULAR).first()
            assertEquals(listOf(1, 2, 3), shows.map { it.id })
        }

    @Test
    fun `loadTvShowsNextPage is a no-op once all pages are loaded`() = runTest(testDispatcher) {
        // totalPages = 1, so after the first load the second call must short-circuit.
        remote.defaultPage = AppResult.Success(
            RemoteTvShowsPage(page = 1, totalPages = 1, results = listOf(tvShow(1, "Only")))
        )

        repository.loadTvShowsNextPage(TvShowCategory.POPULAR)
        val secondResult = repository.loadTvShowsNextPage(TvShowCategory.POPULAR)

        assertTrue(secondResult is AppResult.Success)
        // Only one network call: the guard short-circuits before fetching again.
        assertEquals(1, remote.fetchedPages.count { it.first == "/tv/popular" })
    }

    @Test
    fun `loadTvShowsNextPage propagates a remote error and leaves the cache empty`() =
        runTest(testDispatcher) {
            remote.defaultPage = AppResult.Error("page boom")

            val result = repository.loadTvShowsNextPage(TvShowCategory.POPULAR)

            assertTrue(result is AppResult.Error)
            assertTrue(repository.observeTvShows(TvShowCategory.POPULAR).first().isEmpty())
        }

    // --- clearAndReload ---

    @Test
    fun `clearAndReload reloads page one for every category and replaces stale content`() =
        runTest(testDispatcher) {
            // Seed Popular with two pages so currentPage advances beyond 1.
            remote.pageByNumber[1] = AppResult.Success(
                RemoteTvShowsPage(page = 1, totalPages = 5, results = listOf(tvShow(1, "A")))
            )
            remote.pageByNumber[2] = AppResult.Success(
                RemoteTvShowsPage(page = 2, totalPages = 5, results = listOf(tvShow(2, "B")))
            )
            repository.loadTvShowsNextPage(TvShowCategory.POPULAR)
            repository.loadTvShowsNextPage(TvShowCategory.POPULAR)
            assertEquals(2, repository.observeTvShows(TvShowCategory.POPULAR).first().size)

            // After clearing, page 1 is fetched fresh for each category — the prior two-page
            // Popular content is replaced by just page 1, with no leftover gap.
            val result = repository.clearAndReload()

            assertTrue(result is AppResult.Success)
            assertEquals(listOf(1), repository.observeTvShows(TvShowCategory.POPULAR).first().map { it.id })
            // Every category was reloaded from page 1.
            TvShowCategory.entries.forEach { category ->
                assertTrue(remote.fetchedPages.any { it.second == 1 })
            }
        }

    @Test
    fun `clearAndReload reports success when at least one category reloads`() =
        runTest(testDispatcher) {
            remote.resultByPath["/tv/popular"] = AppResult.Success(
                RemoteTvShowsPage(page = 1, totalPages = 1, results = listOf(tvShow(1, "Ok")))
            )
            remote.resultByPath["/tv/on_the_air"] = AppResult.Error("boom")
            remote.resultByPath["/tv/top_rated"] = AppResult.Error("boom")

            val result = repository.clearAndReload()

            assertTrue(result is AppResult.Success)
        }

    @Test
    fun `clearAndReload reports error only when every category fails`() = runTest(testDispatcher) {
        remote.defaultPage = AppResult.Error("boom")

        val result = repository.clearAndReload()

        assertTrue(result is AppResult.Error)
    }
}
