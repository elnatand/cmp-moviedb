package com.elna.moviedb.core.network.client

/**
 * Writes a network log line to the platform's logging facility (Logcat on Android,
 * the console on iOS) instead of raw stdout.
 *
 * Only ever called from the build-gated Ktor [io.ktor.client.plugins.logging.Logging]
 * block in [createHttpClient], so it never runs in release builds.
 */
internal expect fun platformNetworkLog(message: String)
