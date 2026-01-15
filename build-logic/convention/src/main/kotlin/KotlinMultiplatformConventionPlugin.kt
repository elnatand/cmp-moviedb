


import com.elna.moviedb.getAndroidSdkVersions
import com.elna.moviedb.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("androidLibrary").get().get().pluginId)
        }

        val sdkVersions = getAndroidSdkVersions()

        // Configure Kotlin Multiplatform extension
        extensions.configure<KotlinMultiplatformExtension> {
            // Configure iOS targets
            listOf(
                iosArm64(), // for ios devices
                iosSimulatorArm64(), // for ios simulators in Apple silicon Mac computer
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = path.substring(1).replace(':', '-')
                }
            }

            //remove expect actual warning
            compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
        }

        // Configure Android-specific settings in each module's build file
        // Modules should add:
        // kotlin {
        //     androidLibrary {
        //         compileSdk = <version>
        //         minSdk = <version>
        //         compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
        //     }
        // }
    }
}