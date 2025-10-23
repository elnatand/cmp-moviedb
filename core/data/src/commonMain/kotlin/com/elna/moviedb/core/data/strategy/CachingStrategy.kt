package com.elna.moviedb.core.data.strategy

import com.elna.moviedb.core.model.AppResult

/**
 * Strategy interface for implementing different caching patterns.
 *
 * This interface follows the Strategy Pattern and Single Responsibility Principle
 * by separating caching logic from repository business logic.
 *
 * Common implementations:
 * - Offline-first (cache-first, then network)
 * - Network-first (network-first, then cache on failure)
 * - Cache-only (no network)
 * - Network-only (no cache)
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

/**
 * Network-first caching strategy implementation.
 *
 * This strategy prioritizes fresh data from the network:
 * 1. Attempts to fetch from network first
 * 2. On network success, saves to cache and returns data
 * 3. On network failure, falls back to cache
 * 4. Returns network data if available, otherwise cached data
 * 5. Returns error only if both network and cache fail
 *
 * Benefits:
 * - Always attempts to get fresh data
 * - Graceful degradation to cache on network failure
 * - Best for frequently changing data
 *
 * Use this for:
 * - Social feeds, trending content
 * - Real-time data where staleness is not acceptable
 * - When you want fresh data but with offline fallback
 */
internal class NetworkFirstCachingStrategy : CachingStrategy {
    override suspend fun <T> execute(
        fetchFromCache: suspend () -> T?,
        fetchFromNetwork: suspend () -> AppResult<T>,
        saveToCache: suspend (T) -> Unit
    ): AppResult<T> {
        // Step 1: Try network first
        val networkResult = fetchFromNetwork()

        // Step 2: If network succeeded, save to cache and return
        if (networkResult is AppResult.Success) {
            saveToCache(networkResult.data)
            return networkResult
        }

        // Step 3: Network failed - fallback to cache
        val cachedData = fetchFromCache()
        if (cachedData != null) {
            return AppResult.Success(cachedData)
        }

        // Step 4: Both network and cache failed
        return networkResult // Return the network error
    }
}

/**
 * Cache-only strategy implementation.
 *
 * This strategy only uses cached data and never fetches from network.
 *
 * Use this for:
 * - Offline mode
 * - Testing with mock data
 * - When network is explicitly disabled
 */
internal class CacheOnlyStrategy : CachingStrategy {
    override suspend fun <T> execute(
        fetchFromCache: suspend () -> T?,
        fetchFromNetwork: suspend () -> AppResult<T>,
        saveToCache: suspend (T) -> Unit
    ): AppResult<T> {
        val cachedData = fetchFromCache()
        return if (cachedData != null) {
            AppResult.Success(cachedData)
        } else {
            AppResult.Error("No cached data available")
        }
    }
}

/**
 * Network-only strategy implementation.
 *
 * This strategy always fetches from network and optionally saves to cache.
 *
 * Use this for:
 * - Force refresh scenarios
 * - One-time fetch operations
 * - When cache should not be used
 */
internal class NetworkOnlyStrategy : CachingStrategy {
    override suspend fun <T> execute(
        fetchFromCache: suspend () -> T?,
        fetchFromNetwork: suspend () -> AppResult<T>,
        saveToCache: suspend (T) -> Unit
    ): AppResult<T> {
        val networkResult = fetchFromNetwork()

        if (networkResult is AppResult.Success) {
            saveToCache(networkResult.data)
        }

        return networkResult
    }
}
