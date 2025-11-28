plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
    alias(libs.plugins.kotlinxSerialization)  // for navigation routes
}

android {
    namespace = "com.elna.moviedb.core.ui"
}

dependencies{
    debugImplementation("org.jetbrains.compose.ui:ui-tooling:1.10.0-beta02")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
        }
        commonMain.dependencies {
            api("org.jetbrains.compose.runtime:runtime:1.10.0-beta02")
            api("org.jetbrains.compose.foundation:foundation:1.10.0-beta02")
            api("org.jetbrains.compose.material3:material3:1.9.0-beta03")
            api("org.jetbrains.compose.ui:ui:1.10.0-beta02")
            api("org.jetbrains.compose.components:components-resources:1.10.0-beta02")
            api("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            api("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0-beta02")
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
