plugins {
    alias(libs.plugins.moviedb.androidLibrary)
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

android {
    namespace = "com.example.moviedb.core.common"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime) // for coroutines dispatchers
            implementation(libs.koin.core)
        }
    }
}
