plugins {
    alias(libs.plugins.moviedb.androidLibrary)
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

android {
    namespace = "com.example.moviedb.core.ui"
}

dependencies{
    debugImplementation(compose.uiTooling)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.accompanist.permissions)
            implementation(compose.preview)
        }
        commonMain.dependencies {
            api(libs.koin.compose.viewmodel)
            api(libs.koin.compose.navigation)

            api(libs.coil.compose)
            api(libs.coil.ktor)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.example.moviedb.resources"
    generateResClass = always
}
