plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {

    applyDefaultHierarchyTemplate()
    jvm()

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
                implementation(projects.model)

                api(libs.ktor.client.core)
                api(libs.ktor.client.content.negotiation)
            }
        }
    }
}
