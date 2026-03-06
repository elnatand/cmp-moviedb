
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    android {
        namespace = "com.elna.moviedb.tvshows"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.tvShows.domain)

            implementation(projects.core.ui)
            implementation(projects.core.model)

            implementation(libs.koin.core)
        }
    }
}
