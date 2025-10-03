plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

android {
    namespace = "com.elna.moviedb.core.common"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime) // for coroutines dispatchers
            implementation(libs.koin.core)
        }
    }
}
