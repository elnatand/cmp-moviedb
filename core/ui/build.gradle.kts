plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
    alias(libs.plugins.kotlinxSerialization)  // for navigation routes
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.core.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()


        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }

        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    sourceSets {
        androidMain.dependencies {
            // Tooling support - the new plugin doesn't support debugImplementation
            implementation(libs.compose.ui.tooling)
        }
        commonMain.dependencies {
            api(libs.compose.material3)
            api(libs.compose.material.icons.extended)
            api(libs.compose.resources)
            api(libs.compose.ui.tooling.preview)

            api(libs.navigation.ui)
            api(libs.lifecycle.viewmodel.navigation3)
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
