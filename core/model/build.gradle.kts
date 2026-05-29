
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.elna.moviedb.core.model"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }
}
