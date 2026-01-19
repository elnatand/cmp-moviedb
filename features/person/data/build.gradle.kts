
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.person.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.person.domain)

            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.datastore)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
