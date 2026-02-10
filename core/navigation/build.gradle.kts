plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.core.navigation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.navigation.ui)
            api(libs.lifecycle.viewmodel.navigation3)
            api(libs.koin.compose.navigation)
        }
    }
}
