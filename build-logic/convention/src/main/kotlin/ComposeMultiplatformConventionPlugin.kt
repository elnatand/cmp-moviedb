import com.example.moviedb.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("composeMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("composeCompiler").get().get().pluginId)
        }

        val dependencies = extensions.getByType<ComposeExtension>().dependencies

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                commonMain {
                    dependencies {
                        implementation(dependencies.runtime)
                        implementation(dependencies.foundation)
                        implementation(dependencies.material)
                        implementation(dependencies.material3)
                        implementation(dependencies.materialIconsExtended)
                        implementation(dependencies.ui)
                        implementation(dependencies.components.resources)
                        implementation(dependencies.components.uiToolingPreview)
                    }
                }
            }
        }
    }
}