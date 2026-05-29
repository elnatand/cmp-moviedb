package com.elna.moviedb.feature.movies

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.datastore.FakeAppSettingsPreferences
import com.elna.moviedb.core.datastore.language.LanguageChangeCoordinator
import com.elna.moviedb.core.datastore.language.LanguageProvider
import com.elna.moviedb.core.datastore.pagination.PaginationState
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.movies.model.MovieCategory
import com.elna.moviedb.feature.movies.model.RemoteMovie
import com.elna.moviedb.feature.movies.model.RemoteMoviesPage
import com.elna.moviedb.feature.movies.repositories.MoviesRepositoryImpl
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
class MoviesRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var remote: FakeMoviesRemoteDataSource
    private lateinit var local: FakeMoviesLocalDataSource
    private lateinit var pagination: FakePaginationPreferences
    private lateinit var repository: MoviesRepositoryImpl

    @BeforeTest
    fun setup() {
        // LanguageChangeCoordinator launches a collector on Dispatchers.Main.
        Dispatchers.setMain(testDispatcher)
        remote = FakeMoviesRemoteDataSource()
        local = FakeMoviesLocalDataSource()
        pagination = FakePaginationPreferences()
        val prefs = FakeAppSettingsPreferences()
        repository = MoviesRepositoryImpl(
            remoteDataSource = remote,
            localDataSource = local,
            paginationPreferences = pagination,
            languageProvider = LanguageProvider(prefs),
            languageChangeCoordinator = LanguageChangeCoordinator(prefs, AppDispatchers),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- getMovieDetails: offline-first ---

    @Test
    fun `complete network result is persisted and a second read hits the cache`() =
        runTest(testDispatcher) {
            remote.detailsResult =
                AppResult.Success(FakeMoviesRemoteDataSource.remoteDetails(1, "Inception"))

            val first = repository.getMovieDetails(1)
            assertTrue(first is AppResult.Success)
            assertEquals("Inception", first.data.title)
            assertEquals(1, remote.detailsCallCount)

            // Second call must be served from cache — no additional network fetch.
            val second = repository.getMovieDetails(1)
            assertTrue(second is AppResult.Success)
            assertEquals("Inception", second.data.title)
            assertEquals(1, remote.detailsCallCount)
        }

    @Test
    fun `a partial result (failed videos) is not cached and is refetched next time`() =
        runTest(testDispatcher) {
            remote.detailsResult =
                AppResult.Success(FakeMoviesRemoteDataSource.remoteDetails(2, "Partial"))
            remote.videosResult = AppResult.Error("videos boom")

            val first = repository.getMovieDetails(2)
            assertTrue(first is AppResult.Success) // degrades gracefully

            // Not cached, so the next visit refetches rather than serving an incomplete row.
            repository.getMovieDetails(2)
            assertEquals(2, remote.detailsCallCount)
        }

    @Test
    fun `details network error propagates`() = runTest(testDispatcher) {
        remote.detailsResult = AppResult.Error("details boom")

        val result = repository.getMovieDetails(3)

        assertTrue(result is AppResult.Error)
    }

    // --- loadMoviesNextPage ---

    @Test
    fun `loadMoviesNextPage is a no-op once all pages are loaded`() = runTest(testDispatcher) {
        pagination.setState(
            MovieCategory.POPULAR.name,
            PaginationState(currentPage = 3, totalPages = 3)
        )

        val result = repository.loadMoviesNextPage(MovieCategory.POPULAR)

        assertTrue(result is AppResult.Success)
        assertTrue(remote.fetchedPages.isEmpty()) // guard short-circuits before the network
    }

    @Test
    fun `loadMoviesNextPage inserts entities with stable cross-page positions and saves state`() =
        runTest(testDispatcher) {
            remote.pageByNumber[1] = AppResult.Success(
                RemoteMoviesPage(
                    page = 1,
                    totalPages = 5,
                    results = listOf(movie(10, "A"), movie(11, "B"))
                )
            )

            val result = repository.loadMoviesNextPage(MovieCategory.POPULAR)
            assertTrue(result is AppResult.Success)

            // position = page * 1000 + index (PAGE_ORDER_STRIDE), preserving API order across pages.
            assertEquals(2, local.insertedMovies.size)
            assertEquals(1000, local.insertedMovies[0].position)
            assertEquals(1001, local.insertedMovies[1].position)
            assertEquals(MovieCategory.POPULAR.name, local.insertedMovies[0].category)

            // Saved state advances currentPage to 1 and records totalPages from the response.
            val saved = pagination.getPaginationState(MovieCategory.POPULAR.name).first()
            assertEquals(1, saved.currentPage)
            assertEquals(5, saved.totalPages)
        }

    @Test
    fun `loadMoviesNextPage propagates a remote error without saving state`() =
        runTest(testDispatcher) {
            remote.defaultPage = AppResult.Error("page boom")

            val result = repository.loadMoviesNextPage(MovieCategory.POPULAR)

            assertTrue(result is AppResult.Error)
            assertTrue(local.insertedMovies.isEmpty())
        }

    // --- clearAndReload ---

    @Test
    fun `clearAndReload clears caches and reloads all categories on success`() =
        runTest(testDispatcher) {
            val result = repository.clearAndReload()

            assertTrue(result is AppResult.Success)
            assertEquals(1, local.clearMoviesListCount)
            assertEquals(1, local.clearMovieDetailsCount)
            assertEquals(1, pagination.clearAllCount)
            // One reload per category.
            assertEquals(MovieCategory.entries.size, remote.fetchedPages.size)
        }

    @Test
    fun `clearAndReload reports success when at least one category reloads`() =
        runTest(testDispatcher) {
            // Popular succeeds; the other two fail.
            remote.resultByPath["/movie/popular"] = AppResult.Success(
                RemoteMoviesPage(page = 1, totalPages = 1, results = listOf(movie(1, "Ok")))
            )
            remote.resultByPath["/movie/top_rated"] = AppResult.Error("boom")
            remote.resultByPath["/movie/now_playing"] = AppResult.Error("boom")

            val result = repository.clearAndReload()

            assertTrue(result is AppResult.Success)
        }

    @Test
    fun `clearAndReload reports error only when every category fails`() = runTest(testDispatcher) {
        remote.defaultPage = AppResult.Error("boom")

        val result = repository.clearAndReload()

        assertTrue(result is AppResult.Error)
    }

    private fun movie(id: Int, title: String) =
        RemoteMovie(id = id, title = title, posterPath = null)
}
