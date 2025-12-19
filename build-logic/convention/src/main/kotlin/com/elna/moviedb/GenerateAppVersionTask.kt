package com.elna.moviedb

import com.android.build.gradle.LibraryExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Plugin that auto-configures app version generation for Kotlin Multiplatform modules.
 *
 * This plugin automatically:
 * - Reads version from libs.versions.toml (app-version)
 * - Derives package name from android.namespace + ".utils"
 * - Generates GeneratedAppVersion.kt file
 * - Adds generated source to commonMain source set
 */
class AppVersionGenerationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.afterEvaluate {
            // Get namespace from android block
            val android = runCatching {
                project.extensions.getByType<LibraryExtension>()
            }.getOrNull() ?: return@afterEvaluate

            val namespace = android.namespace ?: return@afterEvaluate

            // Read version from version catalog
            val versionCatalog = project.extensions.findByType(
                org.gradle.api.artifacts.VersionCatalogsExtension::class.java
            ) ?: return@afterEvaluate

            val libs = versionCatalog.find("libs").orElse(null) ?: return@afterEvaluate
            val appVersion = libs.findVersion("app-version").orElse(null) ?: return@afterEvaluate

            // Register task
            val generateTask = project.tasks.register<GenerateAppVersionTask>("generateAppVersion") {
                this.appVersion.set(appVersion.requiredVersion)
                this.packageName.set("$namespace.utils")
                this.outputDir.set(project.layout.buildDirectory.dir("generated/appVersion/commonMain/kotlin"))
            }

            // Add generated source to commonMain
            project.extensions.configure<KotlinMultiplatformExtension>("kotlin") {
                sourceSets.named("commonMain") {
                    kotlin.srcDir(generateTask.map { it.outputs.files.singleFile })
                }
            }
        }
    }
}

/**
 * Gradle task to generate a Kotlin file containing the app version.
 * The version is read from the version catalog and written as a constant.
 */
abstract class GenerateAppVersionTask : DefaultTask() {

    @get:Input
    abstract val appVersion: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val pkg = packageName.get()
        val pkgPath = pkg.replace('.', '/')
        val file = outputDir.get().asFile.resolve("$pkgPath/GeneratedAppVersion.kt")

        file.parentFile.mkdirs()
        file.writeText("""
            package $pkg

            const val APP_VERSION = "${appVersion.get()}"
        """.trimIndent())
    }
}
