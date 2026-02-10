plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.feature.profile.api"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:navigation"))
            implementation(libs.koin.core)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
    }
}
