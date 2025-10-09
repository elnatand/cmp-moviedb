package com.elna.moviedb.core.analytics

import android.os.Bundle
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics


/**
 * Android implementation of AnalyticsService using Firebase Analytics
 */
class FirebaseAnalyticsService : AnalyticsService {

    private val firebaseAnalytics: FirebaseAnalytics? by lazy {
        try {
            val analytics = Firebase.analytics
            // Explicitly enable analytics since we disabled auto-collection in manifest
            analytics.setAnalyticsCollectionEnabled(true)
            Log.d("FirebaseAnalytics", "Firebase Analytics enabled")
            analytics
        } catch (e: Exception) {
            Log.w("FirebaseAnalytics", "Firebase initialization failed: ${e.message}")
            null
        }
    }

    override val isEnabled: Boolean
        get() = firebaseAnalytics != null

    override fun logScreenView(screenName: String, screenClass: String?) {
        firebaseAnalytics?.let { analytics ->
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                screenClass?.let { putString(FirebaseAnalytics.Param.SCREEN_CLASS, it) }
            }
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        firebaseAnalytics?.let { analytics ->
            val bundle = Bundle().apply {
                params.forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Double -> putDouble(key, value)
                        is Boolean -> putBoolean(key, value)
                        else -> putString(key, value.toString())
                    }
                }
            }
            analytics.logEvent(eventName, bundle)
        }
    }

    override fun setUserProperty(name: String, value: String) {
        firebaseAnalytics?.setUserProperty(name, value)
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics?.setUserId(userId)
    }
}
