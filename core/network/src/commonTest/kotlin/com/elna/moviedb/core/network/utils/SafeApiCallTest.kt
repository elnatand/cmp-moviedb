package com.elna.moviedb.core.network.utils

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.DataError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SafeApiCallTest {

    @Test
    fun `wraps a returned value in Success`() = runTest {
        val result = safeApiCall { 42 }

        assertTrue(result is AppResult.Success)
        assertEquals(42, result.data)
    }

    @Test
    fun `classifies an IOException as a NETWORK error`() = runTest {
        val result = safeApiCall<Int> { throw IOException("socket closed") }

        assertTrue(result is AppResult.Error)
        assertEquals(DataError.NETWORK, result.type)
    }

    @Test
    fun `classifies a SerializationException as UNKNOWN (not a connectivity problem)`() = runTest {
        val result = safeApiCall<Int> { throw SerializationException("bad json") }

        assertTrue(result is AppResult.Error)
        assertEquals(DataError.UNKNOWN, result.type)
    }

    @Test
    fun `classifies any other exception as UNKNOWN`() = runTest {
        val result = safeApiCall<Int> { throw IllegalStateException("unexpected") }

        assertTrue(result is AppResult.Error)
        assertEquals(DataError.UNKNOWN, result.type)
    }

    @Test
    fun `rethrows CancellationException so structured concurrency is honored`() = runTest {
        // If safeApiCall swallowed CancellationException, this async would complete normally
        // instead of failing — assertFailsWith pins the rethrow.
        val deferred = async {
            safeApiCall<Int> { throw CancellationException("cancelled") }
        }

        assertFailsWith<CancellationException> { deferred.await() }
    }

    @Test
    fun `error carries the throwable for diagnostics`() = runTest {
        val boom = IOException("timeout")

        val result = safeApiCall<Int> { throw boom }

        assertTrue(result is AppResult.Error)
        assertEquals(boom, result.throwable)
    }
}
