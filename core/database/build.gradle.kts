plugins {
    id("moviedb.android.library")
    id("moviedb.kotlin.multiplatform")
    alias(libs.plugins.sqldelight)
}

//android section should be before kotlin section
android {
    namespace = "com.example.moviedb.core.database"
}

sqldelight {
    databases {
        create(name = "MovieDbDatabase") {
            packageName.set("com.example.moviedb.core.database")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.koin.core)
            implementation(projects.core.model)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}
