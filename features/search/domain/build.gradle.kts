plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.elna.moviedb.search.domain"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)

            implementation(libs.kotlinx.coroutines.core)
        }
    }
}