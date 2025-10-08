plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
    alias(libs.plugins.kotlinxSerialization)  // for navigation routes
}

android {
    namespace = "com.elna.moviedb.core.ui"
}

dependencies{
    debugImplementation(compose.uiTooling)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
        }
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
            api(compose.ui)
            api(compose.components.resources)
            api(compose.materialIconsExtended)
            api(compose.components.uiToolingPreview)
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
