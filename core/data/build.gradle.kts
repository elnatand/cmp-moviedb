plugins {
    id("moviedb.android.library")
    id("moviedb.kotlin.multiplatform")
    id("moviedb.kotlin.composeMultiplatform")
    alias(libs.plugins.kotlinxSerialization)
}


android {
    namespace = "com.example.moviedb.core.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.common)
            implementation(projects.core.database)

            implementation(libs.koin.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
