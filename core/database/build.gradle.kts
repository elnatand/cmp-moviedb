plugins {
    id("moviedb.android.library")
    id("moviedb.kotlin.multiplatform")
    id("app.cash.sqldelight") version "2.0.1"
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



private val sqlDelightVersion = "2.0.1"

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("app.cash.sqldelight:coroutines-extensions:$sqlDelightVersion")
                implementation(libs.koin.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("app.cash.sqldelight:android-driver:$sqlDelightVersion")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("app.cash.sqldelight:native-driver:$sqlDelightVersion")
            }
        }
    }
}
