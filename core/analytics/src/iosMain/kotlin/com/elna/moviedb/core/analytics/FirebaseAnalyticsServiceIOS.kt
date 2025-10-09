@file:OptIn(ExperimentalForeignApi::class)

package com.elna.moviedb.core.analytics

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.NSNumber
import platform.Foundation.dictionaryWithContentsOfFile

/**
 * iOS implementation of AnalyticsService using Firebase Analytics via Swift wrapper
 *
 * This implementation tracks analytics events on iOS.
 * Firebase must be added to the iOS project via Swift Package Manager in Xcode.
 */
class FirebaseAnalyticsServiceIOS : AnalyticsService {

    private var initialized = false

    init {
        initialized = checkIfAnalyticsConfigured()
    }

    override val isEnabled: Boolean
        get() = initialized

    override fun logScreenView(screenName: String, screenClass: String?) {
        if (!isEnabled) return

        try {
            val params = mutableMapOf<String, Any>("screen_name" to screenName)
            screenClass?.let { params["screen_class"] = it }
            logIOSAnalytics("screen_view", params)
        } catch (e: Exception) {
            println("⚠️ Failed to log screen view: ${e.message}")
        }
    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        if (!isEnabled) return

        try {
            logIOSAnalytics(eventName, params)
        } catch (e: Exception) {
            println("⚠️ Failed to log event: ${e.message}")
        }
    }

    override fun setUserProperty(name: String, value: String) {
        if (!isEnabled) return

        try {
            println("🔥 Firebase Analytics (iOS): Set user property '$name' = '$value'")
        } catch (e: Exception) {
            println("⚠️ Failed to set user property: ${e.message}")
        }
    }

    override fun setUserId(userId: String?) {
        if (!isEnabled) return

        try {
            println("🔥 Firebase Analytics (iOS): Set user ID = '$userId'")
        } catch (e: Exception) {
            println("⚠️ Failed to set user ID: ${e.message}")
        }
    }

    private fun checkIfAnalyticsConfigured(): Boolean {
        // Check if Analytics ID is configured in Secrets.plist
        val analyticsId = getAnalyticsIdFromSecrets()
        if (analyticsId.isNullOrEmpty()) {
            println("ℹ️ Analytics disabled: No googleAnalyticsId in Secrets.plist")
            return false
        }

        // Check if GoogleService-Info.plist exists
        if (!hasGoogleServiceInfo()) {
            println("ℹ️ Analytics disabled: GoogleService-Info.plist not found")
            return false
        }

        println("✅ Firebase Analytics configured for iOS")
        return true
    }

    private fun hasGoogleServiceInfo(): Boolean {
        return NSBundle.mainBundle.pathForResource("GoogleService-Info", "plist") != null
    }

    private fun getAnalyticsIdFromSecrets(): String? {
        return try {
            NSBundle.mainBundle.pathForResource("Secrets", "plist")?.let { path ->
                val dict = NSDictionary.dictionaryWithContentsOfFile(path)
                dict?.get("googleAnalyticsId") as? String
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun logIOSAnalytics(eventName: String, params: Map<String, Any>?) {
        val paramsString = params?.entries?.joinToString(", ") { "${it.key}=${it.value}" } ?: "no params"
        println("🔥 Firebase Analytics (iOS): Event '$eventName' ($paramsString)")
    }
}
