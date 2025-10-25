package com.elna.moviedb.core.data.strategy

import com.elna.moviedb.core.model.AppResult

/**
 * Strategy interface for implementing different caching patterns.
 *
 * This interface follows the Strategy Pattern by separating caching logic
 * from repository business logic.
 *
 *
 * Usage example:
 * ```kotlin
 * val result = cachingStrategy.execute(
 *     fetchFromCache = { localDataSource.getMovie(id) },
 *     fetchFromNetwork = { remoteDataSource.getMovie(id) },
 *     saveToCache = { movie -> localDataSource.saveMovie(movie) }
 * )
 * ```
 */
interface CachingStrategy {
    /**
     * Executes a caching strategy to retrieve data.
     *
     * @param T The type of data being cached
     * @param fetchFromCache Lambda to fetch data from local cache. Returns null if not cached.
     * @param fetchFromNetwork Lambda to fetch data from network/remote source
     * @param saveToCache Lambda to save fetched network data to cache
     * @return AppResult<T> Success with data or Error if both cache and network fail
     */
    suspend fun <T> execute(
        fetchFromCache: suspend () -> T?,
        fetchFromNetwork: suspend () -> AppResult<T>,
        saveToCache: suspend (T) -> Unit
    ): AppResult<T>
}

/**
 * Offline-first caching strategy implementation.
 *
 * This strategy prioritizes cached data for fast response times and offline support:
 * 1. Attempts to fetch from cache first
 * 2. If cache hit, returns cached data immediately
 * 3. If cache miss, fetches from network
 * 4. On successful network fetch, saves to cache for future use
 * 5. Returns network result (success or error)
 *
 * Benefits:
 * - Fast response time (cache is instant)
 * - Offline support (works without network if data is cached)
 * - Automatic cache population
 *
 * Use this for:
 * - Movie details, cast, trailers (data that changes infrequently)
 * - User preferences and settings
 * - Any data where stale data is acceptable for offline use
 */
internal class OfflineFirstCachingStrategy : CachingStrategy {
    override suspend fun <T> execute(
        fetchFromCache: suspend () -> T?,
        fetchFromNetwork: suspend () -> AppResult<T>,
        saveToCache: suspend (T) -> Unit
    ): AppResult<T> {
        // Step 1: Check cache first (offline-first)
        val cachedData = fetchFromCache()
        if (cachedData != null) {
            return AppResult.Success(cachedData)
        }

        // Step 2: Cache miss - fetch from network
        val networkResult = fetchFromNetwork()

        // Step 3: If network fetch succeeded, save to cache
        if (networkResult is AppResult.Success) {
            saveToCache(networkResult.data)
        }

        // Step 4: Return network result (whether success or error)
        return networkResult
    }
}
