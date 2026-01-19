
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.search.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.search.domain)
            implementation(projects.features.tvShows.domain)
            implementation(projects.features.movies.domain)

            implementation(projects.core.model)
            implementation(projects.core.datastore)
            implementation(projects.core.network)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}