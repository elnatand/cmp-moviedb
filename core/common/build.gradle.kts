import com.elna.moviedb.AppVersionGenerationPlugin

plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

android {
    namespace = "com.elna.moviedb.core.common"
}

// Apply app version generation
apply<AppVersionGenerationPlugin>()

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core) // for AppDispatchers
            implementation(libs.koin.core)
        }
    }
}
