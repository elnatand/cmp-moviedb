plugins {
    id("moviedb.android.library")
    id("moviedb.kotlin.multiplatform")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.mokoResources)
}

kotlin {
    sourceSets {
        androidMain {
            // Required for moko-resources to work
            dependsOn(commonMain.get())
        }
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material)
            api(compose.material3)
            api(compose.materialIconsExtended)

            api(libs.moko.resources.compose)
            api(libs.rebugger) //logs for recompositions
        }
    }
}

android {
    namespace = "com.example.moviedb.core.ui"
}

multiplatformResources {
    multiplatformResourcesPackage = "com.example.moviedb.ui"
}