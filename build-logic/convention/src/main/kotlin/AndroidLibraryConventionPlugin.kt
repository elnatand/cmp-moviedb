import com.android.build.gradle.LibraryExtension
import com.example.moviedb.Versions
import com.example.moviedb.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure


class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {
                configureAndroid(this)
                defaultConfig.targetSdk = Versions.TARGET_SDK
            }
        }
    }
}
