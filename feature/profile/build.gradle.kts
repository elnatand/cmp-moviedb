plugins {
    id("moviedb.android.library")
    id("moviedb.kotlin.multiplatform")
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.data)
            implementation(projects.core.ui)

            implementation(libs.kamel)
            implementation(libs.koin.core)

            implementation(libs.precompose)
            implementation(libs.precompose.viewmodel) // For ViewModel intergration
            implementation(libs.precompose.koin) // For Koin intergration
        }
    }
}

android {
    namespace = "com.example.moviedb.profile"
}