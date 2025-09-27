package com.elna.moviedb

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Extension to get Android SDK versions from version catalog
 */
fun Project.getAndroidSdkVersions(): AndroidSdkVersions {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    return AndroidSdkVersions(
        compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt(),
        minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt(),
        targetSdk = libs.findVersion("android-targetSdk").get().requiredVersion.toInt()
    )
}

/**
 * Data class to hold Android SDK version information
 */
data class AndroidSdkVersions(
    val compileSdk: Int,
    val minSdk: Int,
    val targetSdk: Int
)
