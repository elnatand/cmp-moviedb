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
        val commonMain by getting {
            dependencies {
                implementation(libs.sqldelight.coroutines.extensions)
                implementation(libs.koin.core)
                implementation(projects.core.model)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.android.driver)
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
                implementation(libs.sqldelight.native.driver)

                /**
                 *  see why need this dependency
                 *  https://github.com/cashapp/sqldelight/issues/4888#issuecomment-1846036472
                 */
                implementation("co.touchlab:stately-common:2.0.6")
            }
        }
    }
}
