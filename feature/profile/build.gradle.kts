plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("moviedb.android.library")
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            path.substring(1).replace(':', '-')
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.data)
            implementation(projects.core.ui)

            implementation(libs.kamel)
            implementation(libs.koin.core)

            implementation(libs.precompose)
            implementation(libs.precompose.viewmodel) // For ViewModel intergration
            implementation(libs.precompose.koin) // For Koin intergration
        }
    }
}

android {
    namespace = "com.example.moviedb.profile"
}