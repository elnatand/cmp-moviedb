
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    android {
        namespace = "com.elna.moviedb.search"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.search.domain)
            implementation(projects.features.tvShows.domain)
            implementation(projects.features.movies.domain)

            implementation(projects.core.model)
            implementation(projects.core.ui)

            implementation(libs.koin.core)
        }
    }
}