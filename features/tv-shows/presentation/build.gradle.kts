
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    android {
        namespace = "com.elna.moviedb.tvshows"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        // Opt in to host-side (JVM) unit tests so commonTest runs under `./gradlew test`,
        // not only on the iOS sim target. Disabled by default in AGP 9's KMP library plugin.
        withHostTest { }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.tvShows.domain)

            implementation(projects.core.ui)
            implementation(projects.core.model)

            implementation(libs.koin.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
