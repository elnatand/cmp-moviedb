import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.composeapp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    // iOS targets are configured by the convention plugin
    // Customize the framework configuration
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.withType<Framework>().configureEach {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.elna.moviedb")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.data)
            implementation(projects.core.database)
            implementation(projects.core.datastore)
            implementation(projects.core.ui)

            implementation(projects.features.movies)

            implementation(projects.features.tvShows.presentation)
            implementation(projects.features.tvShows.data)

            implementation(projects.features.search.presentation)
            implementation(projects.features.search.data)

            implementation(projects.features.profile.presentation)

            implementation(projects.features.person.presentation)
            implementation(projects.features.person.data)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}
