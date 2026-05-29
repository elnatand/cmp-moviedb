
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.elna.moviedb.core.datastore"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        // Opt in to host-side (JVM) unit tests so commonTest runs under `./gradlew test`,
        // not only on the iOS sim target. Disabled by default in AGP 9's KMP library plugin.
        withHostTest { }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.common)

            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
