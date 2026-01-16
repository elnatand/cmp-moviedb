import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

dependencies {
    implementation(projects.composeApp)
    implementation(projects.core.network)
    implementation(projects.core.ui)

    implementation(libs.koin.android)
}

kotlin {
    android {
        namespace = "com.elna.moviedb"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        defaultConfig {
            applicationId = "com.elna.moviedb"
            minSdk = libs.versions.android.minSdk.get().toInt()
            targetSdk = libs.versions.android.targetSdk.get().toInt()
            versionCode = libs.versions.app.build.get().toInt()
            versionName = libs.versions.app.version.get()
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}
