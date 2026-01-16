import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.core.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.common)
            implementation(projects.core.network)
            implementation(projects.core.database)
            implementation(projects.core.datastore)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
