plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

android {
    namespace = "com.elna.moviedb.core.datastore"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)

            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
