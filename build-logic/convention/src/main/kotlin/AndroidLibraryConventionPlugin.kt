import com.android.build.gradle.LibraryExtension
import com.elna.moviedb.configureAndroid
import com.elna.moviedb.getAndroidSdkVersions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure


class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }


        }
    }
}
