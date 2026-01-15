import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.composeapp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

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
        commonMain.dependencies {
//            implementation(projects.core.common)
//            implementation(projects.core.model)
//            implementation(projects.core.network)
//            implementation(projects.core.data)
//            implementation(projects.core.database)
//            implementation(projects.core.datastore)
//            implementation(projects.core.ui)
//
//            implementation(projects.features.movies)
//            implementation(projects.features.tvShows)
//            implementation(projects.features.search)
//            implementation(projects.features.profile)
//            implementation(projects.features.person)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }

    //remove expect actual warning
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}
