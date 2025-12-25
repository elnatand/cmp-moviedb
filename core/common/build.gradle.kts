import com.elna.moviedb.AppVersionGenerationPlugin

plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

// Apply app version generation
apply<AppVersionGenerationPlugin>()

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.core.common"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core) // for AppDispatchers
            implementation(libs.koin.core)
        }
    }
}
