
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.elna.moviedb.person.domain"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
        }
    }
}
