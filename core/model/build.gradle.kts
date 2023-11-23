plugins {
    id("moviedb.kotlin.multiplatform")
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.ktor.serialization.kotlinx.json)
            }
        }
    }
}
