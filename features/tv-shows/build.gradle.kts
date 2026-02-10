plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.tvshows"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.data)
            implementation(projects.core.designsystem)
            implementation(projects.core.common)
            implementation(projects.core.navigation)

            implementation(libs.koin.core)
        }
    }
}
