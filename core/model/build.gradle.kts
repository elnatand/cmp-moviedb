plugins {
    id("moviedb.kotlin.multiplatform")
    id("moviedb.kotlin.composeMultiplatform")
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {

    jvm()

    sourceSets {
        commonMain.dependencies {
            api(libs.ktor.serialization.kotlinx.json)
        }
    }
}
