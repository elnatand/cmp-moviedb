import com.android.build.api.dsl.androidLibrary
import com.elna.moviedb.getAndroidSdkVersions
import com.elna.moviedb.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("androidLibrary").get().get().pluginId)
        }

        // Configure Kotlin Multiplatform extension
        extensions.configure<KotlinMultiplatformExtension> {

            val sdkVersions = getAndroidSdkVersions()
            androidLibrary {
                compileSdk = sdkVersions.compileSdk
                minSdk = sdkVersions.minSdk

                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

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
    }
}