package com.elna.moviedb.core.common.utils

interface AppVersion {
    fun getAppVersion(): String
}

class AppVersionImpl : AppVersion {
    override fun getAppVersion(): String {
        return APP_VERSION
    }
}
