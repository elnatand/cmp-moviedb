package com.elna.moviedb

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
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
        project.afterEvaluate {
            // Read version from version catalog
            val versionCatalog = project.extensions.findByType(
                org.gradle.api.artifacts.VersionCatalogsExtension::class.java
            ) ?: return@afterEvaluate

            val libs = versionCatalog.find("libs").orElse(null) ?: return@afterEvaluate
            val appVersion = libs.findVersion("app-version").orElse(null) ?: return@afterEvaluate
            val appBuild = libs.findVersion("app-build").orElse(null) ?: return@afterEvaluate

            // Locate iOS files
            val infoPlistFile = project.rootProject.file("iosApp/iosApp/Info.plist")
            val pbxprojFile = project.rootProject.file("iosApp/iosApp.xcodeproj/project.pbxproj")

            // Register task for manual execution
            project.tasks.register<UpdateIosVersionTask>("updateIosVersion") {
                this.appVersion.set(appVersion.requiredVersion)
                this.appBuild.set(appBuild.requiredVersion)
                this.infoPlistFile.set(infoPlistFile)
                this.pbxprojFile.set(pbxprojFile)

                group = "versioning"
                description = "Updates iOS version in Info.plist and project.pbxproj from libs.versions.toml"
            }
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

    @get:InputFile
    abstract val infoPlistFile: RegularFileProperty

    @get:InputFile
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

        // Update or add MARKETING_VERSION and CURRENT_PROJECT_VERSION in all buildSettings sections
        val buildSettingsRegex = Regex("(buildSettings = \\{[^}]*?)(\\n\\s*\\};)", RegexOption.DOT_MATCHES_ALL)

        content = buildSettingsRegex.replace(content) { matchResult ->
            val buildSettingsBlock = matchResult.groupValues[1]
            val closingBrace = matchResult.groupValues[2]

            // Check if MARKETING_VERSION already exists
            val hasMarketingVersion = buildSettingsBlock.contains("MARKETING_VERSION")
            val hasCurrentProjectVersion = buildSettingsBlock.contains("CURRENT_PROJECT_VERSION")

            var updatedBlock = buildSettingsBlock

            if (hasMarketingVersion) {
                // Update existing MARKETING_VERSION
                updatedBlock = updatedBlock.replace(
                    Regex("MARKETING_VERSION = .*?;"),
                    "MARKETING_VERSION = $version;"
                )
            } else {
                // Add MARKETING_VERSION before closing brace
                updatedBlock = "$updatedBlock\n\t\t\t\tMARKETING_VERSION = $version;"
            }

            if (hasCurrentProjectVersion) {
                // Update existing CURRENT_PROJECT_VERSION
                updatedBlock = updatedBlock.replace(
                    Regex("CURRENT_PROJECT_VERSION = .*?;"),
                    "CURRENT_PROJECT_VERSION = $build;"
                )
            } else {
                // Add CURRENT_PROJECT_VERSION before closing brace
                updatedBlock = "$updatedBlock\n\t\t\t\tCURRENT_PROJECT_VERSION = $build;"
            }

            updatedBlock + closingBrace
        }

        pbxFile.writeText(content)
        logger.lifecycle("Updated project.pbxproj with MARKETING_VERSION and CURRENT_PROJECT_VERSION")
    }
}
