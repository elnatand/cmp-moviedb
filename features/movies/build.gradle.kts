plugins {
    id("moviedb.android.library")
    id("moviedb.kotlin.multiplatform")
    id("moviedb.kotlin.composeMultiplatform")
    alias(libs.plugins.kotlinxSerialization)
}

//android section should be before kotlin section
android {
    namespace = "com.example.moviedb.movies"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.data)
            implementation(projects.core.ui)

            implementation(libs.koin.core)

            implementation(libs.precompose)
            implementation(libs.navigation.compose)
        }
    }
}
