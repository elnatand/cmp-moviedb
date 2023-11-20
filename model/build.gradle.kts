plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)

   // alias(libs.plugins.androidLibrary)
}

kotlin {

    applyDefaultHierarchyTemplate()
    jvm()
//   androidTarget()
//
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            path.substring(1).replace(':', '-')
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.ktor.serialization.kotlinx.json)
            }
        }
    }
}
