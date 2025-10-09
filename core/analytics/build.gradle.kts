plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

android {
    namespace = "com.elna.moviedb.core.analytics"
}

kotlin {
    // Configure CocoaPods for iOS Firebase
//    cocoapods {
//        summary = "Analytics module with Firebase support"
//        homepage = "https://github.com/example/cmp-moviedb"
//        ios.deploymentTarget = "14.0"
//
//        pod("FirebaseAnalytics") {
//            version = "11.5.0"
//            extraOpts += listOf("-compiler-option", "-fmodules")
//        }
//    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.android.firebase.bom))
            implementation(libs.android.firebase.analytics)
        }
        iosMain.dependencies {
            // Firebase Analytics for iOS configured via CocoaPods above
        }
    }
}
