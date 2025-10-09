package com.elna.moviedb.core.analytics

import cocoapods.FirebaseAnalytics.FIRAnalytics
import platform.Foundation.NSNumber

/**
 * iOS implementation of AnalyticsService using Firebase Analytics
 */
class FirebaseAnalyticsServiceIOS : AnalyticsService {

    override val isEnabled: Boolean = true

    override fun logScreenView(screenName: String, screenClass: String?) {
        val params = mutableMapOf<Any?, Any>(
            "screen_name" to screenName
        )
        screenClass?.let { params["screen_class"] = it }

        FIRAnalytics.logEventWithName("screen_view", params)
    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        val firebaseParams = params.mapValues { (_, value) ->
            when (value) {
                is Int -> NSNumber.numberWithInt(value)
                is Long -> NSNumber.numberWithLongLong(value)
                is Double -> NSNumber.numberWithDouble(value)
                is Boolean -> NSNumber.numberWithBool(value)
                else -> value.toString()
            }
        }

        FIRAnalytics.logEventWithName(eventName, firebaseParams)
    }

    override fun setUserProperty(name: String, value: String) {
        FIRAnalytics.setUserPropertyString(value, name)
    }

    override fun setUserId(userId: String?) {
        FIRAnalytics.setUserID(userId)
    }
}
