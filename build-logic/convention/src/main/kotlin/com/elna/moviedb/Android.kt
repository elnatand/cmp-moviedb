package com.elna.moviedb

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

/**
 * Configures Android-specific settings for both application and library modules
 * Uses version catalog for SDK versions
 */
fun Project.configureAndroid(
    libraryExtension: LibraryExtension
) {
    val sdkVersions = getAndroidSdkVersions()

    libraryExtension.apply {
        compileSdk = sdkVersions.compileSdk

        defaultConfig {
            minSdk = sdkVersions.minSdk
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}
