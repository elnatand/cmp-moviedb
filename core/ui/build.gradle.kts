plugins {
    id("moviedb.android.library")
    id("moviedb.kotlin.multiplatform")
    id("moviedb.kotlin.composeMultiplatform")
}
//android section should be before kotlin section
android {
    namespace = "com.example.moviedb.core.ui"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.accompanist.permissions)
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
