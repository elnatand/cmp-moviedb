
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.movies.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.movies.domain)

            implementation(projects.core.network)
            implementation(projects.core.database)
            implementation(projects.core.datastore)
            implementation(projects.core.model)
            implementation(projects.core.data)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
