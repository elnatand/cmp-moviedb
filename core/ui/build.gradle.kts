plugins {
    id("moviedb.android.library")
    id("moviedb.kotlin.multiplatform")
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material)
            api(compose.material3)
            api(compose.materialIconsExtended)

            api(libs.rebugger) //logs for recompositions
        }
    }
}

android {
    namespace = "com.example.moviedb.core.ui"
}
