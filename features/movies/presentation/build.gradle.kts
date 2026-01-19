
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.movies.presentation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.movies.domain)

            implementation(projects.core.model)
            implementation(projects.core.data)
            implementation(projects.core.ui)

            implementation(libs.koin.core)
        }
    }
}
