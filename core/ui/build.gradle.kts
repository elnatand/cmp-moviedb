plugins {
    id("moviedb.android.library")
    id("moviedb.kotlin.multiplatform")
    alias(libs.plugins.jetbrainsCompose)
}
//android section should be before kotlin section
android {
    namespace = "com.example.moviedb.core.ui"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material)
            api(compose.material3)
            api(compose.materialIconsExtended)

            api(libs.kamel)

            api(libs.rebugger) //logs for recompositions

            api(libs.decompose)
            api(libs.decompose.jetbrains)
//            api(libs.kotlinx.serialization.json)
            api("com.arkivanov.essenty:lifecycle-coroutines:1.3.0")
        }
    }
}
