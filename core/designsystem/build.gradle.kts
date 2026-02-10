plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}


kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.core.designsystem"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        androidResources.enable = true
    }



    sourceSets {
        androidMain.dependencies {
            // Tooling support - runtime only but transitive to consumer modules
            runtimeOnly(libs.compose.ui.tooling)
        }
        commonMain.dependencies {
            implementation(projects.core.model)
            api(libs.compose.material3)
            api(libs.compose.material.icons.extended)
            api(libs.compose.resources)
            api(libs.compose.ui.tooling.preview)

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
