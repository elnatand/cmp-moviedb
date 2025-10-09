plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

android {
    namespace = "com.elna.moviedb.core.analytics"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.android.firebase.bom))
            implementation(libs.android.firebase.analytics)
        }
        iosMain.dependencies {
            // Firebase Analytics for iOS is added via Swift Package Manager in Xcode
            // The Kotlin code will call Swift wrapper functions
        }
    }
}
