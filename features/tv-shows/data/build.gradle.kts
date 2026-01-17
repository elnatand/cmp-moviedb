
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.tvshows.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.data)
            implementation(projects.features.tvShows.presentation)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
