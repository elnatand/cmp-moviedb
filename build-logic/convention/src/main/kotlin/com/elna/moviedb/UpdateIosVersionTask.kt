package com.elna.moviedb

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

/**
 * Plugin that configures iOS version update task.
 *
 * This plugin:
 * - Reads app-version and app-build from libs.versions.toml
 * - Locates iOS Info.plist and project.pbxproj files
 * - Registers updateIosVersion task for manual execution
 *
 * Run manually with: ./gradlew updateIosVersion
 */
class IosVersionUpdatePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register eagerly (not in afterEvaluate): registering tasks during afterEvaluate
        // breaks task-configuration avoidance and hides the task during configuration. The
        // version catalog is already available at apply time.
        val versionCatalog = project.extensions.findByType(VersionCatalogsExtension::class.java)
            ?: return
        val libs = versionCatalog.find("libs").orElse(null) ?: return
        val appVersion = libs.findVersion("app-version").orElse(null) ?: return
        val appBuild = libs.findVersion("app-build").orElse(null) ?: return

        project.tasks.register<UpdateIosVersionTask>("updateIosVersion") {
            this.appVersion.set(appVersion.requiredVersion)
            this.appBuild.set(appBuild.requiredVersion)
            this.infoPlistFile.set(project.rootProject.file("iosApp/iosApp/Info.plist"))
            this.pbxprojFile.set(project.rootProject.file("iosApp/iosApp.xcodeproj/project.pbxproj"))

            group = "versioning"
            description = "Updates iOS version in Info.plist and project.pbxproj from libs.versions.toml"
        }
    }
}

/**
 * Gradle task to update iOS version configuration.
 * Updates both Info.plist and project.pbxproj with version and build number from libs.versions.toml.
 */
abstract class UpdateIosVersionTask : DefaultTask() {

    @get:Input
    abstract val appVersion: Property<String>

    @get:Input
    abstract val appBuild: Property<String>

    // @Internal, not @InputFile: this task edits these files in place (input == output), so
    // declaring them as inputs would be a misleading, never-up-to-date dependency. It's a
    // manual versioning task that always runs.
    @get:Internal
    abstract val infoPlistFile: RegularFileProperty

    @get:Internal
    abstract val pbxprojFile: RegularFileProperty

    @TaskAction
    fun updateVersion() {
        val version = appVersion.get()
        val build = appBuild.get()

        updateInfoPlist(version, build)
        updatePbxproj(version, build)

        logger.lifecycle("Updated iOS version to $version ($build)")
    }

    private fun updateInfoPlist(version: String, build: String) {
        val plistFile = infoPlistFile.get().asFile

        if (!plistFile.exists()) {
            logger.warn("Info.plist file not found at: ${plistFile.absolutePath}")
            return
        }

        val content = plistFile.readText()

        // Update to use Xcode build settings variables
        val updatedContent = content
            .replace(
                Regex("<key>CFBundleShortVersionString</key>\\s*<string>.*?</string>"),
                Regex.escapeReplacement("<key>CFBundleShortVersionString</key>\n\t<string>\$(MARKETING_VERSION)</string>")
            )
            .replace(
                Regex("<key>CFBundleVersion</key>\\s*<string>.*?</string>"),
                Regex.escapeReplacement("<key>CFBundleVersion</key>\n\t<string>\$(CURRENT_PROJECT_VERSION)</string>")
            )

        plistFile.writeText(updatedContent)
        logger.lifecycle("Updated Info.plist to use Xcode build settings")
    }

    private fun updatePbxproj(version: String, build: String) {
        val pbxFile = pbxprojFile.get().asFile

        if (!pbxFile.exists()) {
            logger.warn("project.pbxproj file not found at: ${pbxFile.absolutePath}")
            return
        }

        var content = pbxFile.readText()

        // Simple approach: Replace ALL occurrences of MARKETING_VERSION and CURRENT_PROJECT_VERSION
        // This ensures both project-level and target-level configurations are updated
        content = content.replace(
            Regex("MARKETING_VERSION = [^;]+;"),
            "MARKETING_VERSION = $version;"
        )

        content = content.replace(
            Regex("CURRENT_PROJECT_VERSION = [^;]+;"),
            "CURRENT_PROJECT_VERSION = $build;"
        )

        pbxFile.writeText(content)
        logger.lifecycle("Updated project.pbxproj with MARKETING_VERSION = $version and CURRENT_PROJECT_VERSION = $build")
        logger.lifecycle("Updated both project-level and target-level build configurations")
    }
}
