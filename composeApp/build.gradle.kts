import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    android {
        namespace = "com.elna.moviedb.composeapp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    // iOS targets are declared by the convention plugin, but the framework binary is
    // created here: :composeApp is the only module linked into the iOS app, so it owns
    // the framework the Xcode project embeds.
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
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

            implementation(projects.core.database)
            implementation(projects.core.datastore)
            implementation(projects.core.ui)

            implementation(projects.features.movies.presentation)
            implementation(projects.features.movies.data)

            implementation(projects.features.tvShows.presentation)
            implementation(projects.features.tvShows.data)

            implementation(projects.features.search.presentation)
            implementation(projects.features.search.data)

            implementation(projects.features.profile.presentation)

            implementation(projects.features.person.presentation)
            implementation(projects.features.person.data)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            // Serializes the navigation back stack for rememberSaveable (process-death restore).
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
