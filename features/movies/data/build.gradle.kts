
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "com.elna.moviedb.core.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        // Opt in to host-side (JVM) unit tests so commonTest runs under `./gradlew test`,
        // not only on the iOS sim target. Disabled by default in AGP 9's KMP library plugin.
        withHostTest { }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.movies.domain)

            implementation(projects.core.network)
            implementation(projects.core.database)
            implementation(projects.core.datastore)
            implementation(projects.core.model)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.core)
        }

        commonTest.dependencies {
            implementation(projects.core.common)
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
