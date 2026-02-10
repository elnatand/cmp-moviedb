package com.elna.moviedb.core.common.utils

/**
 * Fake implementation of AppVersion for testing.
 */
class FakeAppVersion : AppVersion {
    override fun getAppVersion(): String = "1.0.0-test"
}