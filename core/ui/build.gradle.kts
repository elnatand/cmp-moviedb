plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("moviedb.android.library")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.mokoResources)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            path.substring(1).replace(':', '-')
            isStatic = true
        }
    }

    sourceSets {
        // Required for moko-resources to work
        applyDefaultHierarchyTemplate()

        androidMain {
            // Required for moko-resources to work
            dependsOn(commonMain.get())
        }
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material)
            api(compose.material3)
            api(compose.materialIconsExtended)

            api(libs.moko.resources.compose)
        }
    }
}

android {
    namespace = "com.example.moviedb.core.ui"
}

multiplatformResources {
    multiplatformResourcesPackage = "com.example.moviedb.ui"
}