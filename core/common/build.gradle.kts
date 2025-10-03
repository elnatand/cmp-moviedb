plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

android {
    namespace = "com.elna.moviedb.core.common"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core) // for AppDispatchers
            implementation(libs.koin.core)
        }
    }
}
