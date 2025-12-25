package com.elna.moviedb.core.common.utils

import com.elna.moviedb.core.common.utils.BuildKonfig.APP_VERSION

interface AppVersion {
    fun getAppVersion(): String
}

class AppVersionImpl : AppVersion {
    override fun getAppVersion(): String {
        return APP_VERSION
    }
}
