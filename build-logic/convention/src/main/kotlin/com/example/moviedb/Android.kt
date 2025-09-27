package com.example.moviedb

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

/**
 * Configures Android-specific settings for both application and library modules
 * Uses version catalog for SDK versions
 */
fun Project.configureAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    val sdkVersions = getAndroidSdkVersions()

    commonExtension.apply {
        compileSdk = sdkVersions.compileSdk

        defaultConfig {
            minSdk = sdkVersions.minSdk
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }
}
