
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.profile.presentation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.data)
            implementation(projects.core.datastore)
            implementation(projects.core.common)
            implementation(projects.core.ui)

            implementation(libs.koin.core)
        }
    }
}
