plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.feature.person"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.person.api)
            implementation(projects.features.tvShows.api)
            implementation(projects.features.movies.api)

            implementation(projects.core.model)
            implementation(projects.core.data)
            implementation(projects.core.designsystem)
            implementation(projects.core.common)
            implementation(projects.core.navigation)

            implementation(libs.koin.core)
        }
    }
}
