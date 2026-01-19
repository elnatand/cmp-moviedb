
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.moviedb.composeMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.person.presentation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.person.domain)

            implementation(projects.core.model)
            implementation(projects.core.ui)

            implementation(libs.koin.core)
        }
    }
}
