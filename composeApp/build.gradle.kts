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
            implementation(projects.core.designsystem)
            implementation(projects.core.navigation)

            implementation(projects.features.movies.api)
            implementation(projects.features.movies.impl)

            implementation(projects.features.tvShows.api)
            implementation(projects.features.tvShows.impl)

            implementation(projects.features.search.api)
            implementation(projects.features.search.impl)

            implementation(projects.features.profile.api)
            implementation(projects.features.profile.impl)

            implementation(projects.features.person.api)
            implementation(projects.features.person.impl)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}
