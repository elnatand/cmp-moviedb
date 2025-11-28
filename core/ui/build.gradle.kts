plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
    alias(libs.plugins.kotlinxSerialization)  // for navigation routes
}

android {
    namespace = "com.elna.moviedb.core.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.compose.material3)
            api(libs.compose.material.icons.extended)
            api(libs.compose.resources)
            api(libs.compose.tooling.preview)

            api(libs.koin.compose.navigation)

            implementation(libs.coil.compose)
            implementation(libs.coil.ktor)
            implementation(libs.coil.svg)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.elna.moviedb.resources"
    generateResClass = always
}
