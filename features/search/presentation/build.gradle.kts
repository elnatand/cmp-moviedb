
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.search.presentation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.search.domain)
            implementation(projects.features.tvShows.domain)

            implementation(projects.core.model)
            implementation(projects.core.ui)

            implementation(libs.koin.core)
        }
    }
}