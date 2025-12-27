import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.elna.moviedb")
        }
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.koin.android)
            }
        }

        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.data)
            implementation(projects.core.database)
            implementation(projects.core.datastore)
            implementation(projects.core.ui)

            implementation(projects.features.movies)
            implementation(projects.features.tvShows)
            implementation(projects.features.search)
            implementation(projects.features.profile)
            implementation(projects.features.person)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }

    //remove expect actual warning
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}

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
