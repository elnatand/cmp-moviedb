import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
        }

        extensions.configure<KotlinMultiplatformExtension> {

            if (pluginManager.hasPlugin("com.android.library") || pluginManager.hasPlugin("com.android.application")) {
                androidTarget()
            }

            listOf(
                iosArm64(), // for ios devices
                // iosX64(), // for ios simulator in Intel silicon Mac computer
                iosSimulatorArm64(), // for ios simulators in Apple silicon Mac computer
            ).forEach { target ->
                target.binaries.framework {
                    baseName = path.substring(1).replace(':', '-')
                }
            }

//            //remove expect actual warning
//            targets.configureEach {
//                compilations.configureEach {
//                    compilerOptions.configure {
//                        freeCompilerArgs.add("-Xexpect-actual-classes")
//                    }
//                }
//            }
        }
    }
}