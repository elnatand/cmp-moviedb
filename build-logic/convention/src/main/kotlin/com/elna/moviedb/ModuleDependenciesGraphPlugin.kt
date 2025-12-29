package com.elna.moviedb

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.time.LocalDate

/**
 * Plugin that generates a Mermaid diagram of module dependencies.
 *
 * Run with: ./gradlew generateModuleDependenciesGraph
 */
class ModuleDependenciesGraphPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Only apply to root project
        if (project != project.rootProject) {
            return
        }

        project.afterEvaluate {
            // Collect dependencies at configuration time (before task execution)
            val moduleDependencies = collectModuleDependencies(project)

            project.tasks.register<GenerateModuleDependenciesGraphTask>(
                "generateModuleDependenciesGraph"
            ) {
                outputFile.set(project.file("docs/MODULE_DEPENDENCIES.md"))
                // Pass the collected dependencies as a serializable map
                dependencies.set(moduleDependencies)

                group = "documentation"
                description = "Generates a Mermaid diagram of module-to-module dependencies"
            }
        }
    }

    private fun collectModuleDependencies(rootProject: Project): Map<String, List<String>> {
        val dependencies = mutableMapOf<String, MutableList<String>>()

        rootProject.subprojects
            .filter { it.path != ":build-logic" }
            .forEach { project ->
                val projectDeps = mutableListOf<String>()

                try {
                    // Try to read from build file directly by parsing dependencies
                    val buildFile = project.buildFile
                    if (buildFile.exists()) {
                        val content = buildFile.readText()

                        // Match patterns like: implementation(projects.core.model)
                        val projectsPattern = Regex("""(?:implementation|api)\s*\(\s*projects\.([\w.]+)\s*\)""")
                        val matches = projectsPattern.findAll(content)

                        matches.forEach { match ->
                            val projectRef = match.groupValues[1]
                            // Convert projects.core.model to :core:model
                            // Also convert camelCase to kebab-case (e.g., tvShows -> tv-shows)
                            val projectPath = ":${projectRef
                                .replace(Regex("([a-z])([A-Z])"), "$1-$2")
                                .lowercase()
                                .replace('.', ':')}"
                            projectDeps.add(projectPath)
                        }
                    }
                } catch (e: Exception) {
                    rootProject.logger.warn("Could not read dependencies for ${project.path}: ${e.message}")
                }

                // Store unique dependencies
                dependencies[project.path] = projectDeps.distinct().toMutableList()
            }

        return dependencies
    }
}

/**
 * Gradle task to generate module dependencies graph in Mermaid format.
 */
abstract class GenerateModuleDependenciesGraphTask : DefaultTask() {

    @get:Input
    abstract val dependencies: org.gradle.api.provider.MapProperty<String, List<String>>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val moduleDependencies = dependencies.get()
        val mermaidGraph = generateMermaidDiagram(moduleDependencies)

        val outputDir = outputFile.get().asFile.parentFile
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        outputFile.get().asFile.writeText(mermaidGraph)
        logger.lifecycle("Module dependencies graph generated at: ${outputFile.get().asFile.path}")
    }

    private fun generateMermaidDiagram(dependencies: Map<String, List<String>>): String {
        val builder = StringBuilder()

        builder.appendLine("# Module Dependencies Graph")
        builder.appendLine()
        builder.appendLine("Last updated: ${LocalDate.now()}")
        builder.appendLine()
        builder.appendLine("This diagram shows the dependencies between modules in the CMP MovieDB project.")
        builder.appendLine()
        builder.appendLine("```mermaid")
        builder.appendLine("graph LR")

        // Classify modules
        val appModules = dependencies.keys.filter { it == ":composeApp" }
        val featureModules = dependencies.keys.filter { it.startsWith(":features:") }.sorted()
        val coreModules = dependencies.keys.filter { it.startsWith(":core:") }.sorted()

        // Generate subgraphs
        if (appModules.isNotEmpty()) {
            builder.appendLine("    subgraph App[\"Application Layer\"]")
            appModules.forEach { module ->
                builder.appendLine("        ${formatNodeId(module)}[\"$module\"]")
            }
            builder.appendLine("    end")
            builder.appendLine()
        }

        if (featureModules.isNotEmpty()) {
            builder.appendLine("    subgraph Features[\"Feature Modules\"]")
            featureModules.forEach { module ->
                builder.appendLine("        ${formatNodeId(module)}[\"${formatModuleName(module)}\"]")
            }
            builder.appendLine("    end")
            builder.appendLine()
        }

        if (coreModules.isNotEmpty()) {
            builder.appendLine("    subgraph Core[\"Core Modules\"]")
            coreModules.forEach { module ->
                builder.appendLine("        ${formatNodeId(module)}[\"${formatModuleName(module)}\"]")
            }
            builder.appendLine("    end")
            builder.appendLine()
        }

        // Generate dependency arrows
        builder.appendLine("    %% Dependencies")

        // Group by source module for better readability
        appModules.forEach { module ->
            val deps = dependencies[module] ?: emptyList()
            if (deps.isNotEmpty()) {
                builder.appendLine("    %% $module dependencies")
                deps.forEach { dep ->
                    builder.appendLine("    ${formatNodeId(module)} --> ${formatNodeId(dep)}")
                }
                builder.appendLine()
            }
        }

        featureModules.forEach { module ->
            val deps = dependencies[module] ?: emptyList()
            if (deps.isNotEmpty()) {
                builder.appendLine("    %% $module dependencies")
                deps.forEach { dep ->
                    builder.appendLine("    ${formatNodeId(module)} --> ${formatNodeId(dep)}")
                }
                builder.appendLine()
            }
        }

        coreModules.forEach { module ->
            val deps = dependencies[module] ?: emptyList()
            if (deps.isNotEmpty()) {
                builder.appendLine("    %% $module dependencies")
                deps.forEach { dep ->
                    builder.appendLine("    ${formatNodeId(module)} --> ${formatNodeId(dep)}")
                }
                builder.appendLine()
            }
        }

        // Generate styling
        builder.appendLine("    %% Styling")
        builder.appendLine("    classDef appStyle fill:#FFF9C4,stroke:#F57C00,stroke-width:2px")
        builder.appendLine("    classDef featureStyle fill:#C8E6C9,stroke:#388E3C,stroke-width:2px")
        builder.appendLine("    classDef coreStyle fill:#BBDEFB,stroke:#1976D2,stroke-width:2px")
        builder.appendLine()

        if (appModules.isNotEmpty()) {
            builder.appendLine("    class ${appModules.joinToString(",") { formatNodeId(it) }} appStyle")
        }
        if (featureModules.isNotEmpty()) {
            builder.appendLine("    class ${featureModules.joinToString(",") { formatNodeId(it) }} featureStyle")
        }
        if (coreModules.isNotEmpty()) {
            builder.appendLine("    class ${coreModules.joinToString(",") { formatNodeId(it) }} coreStyle")
        }

        builder.appendLine("```")
        builder.appendLine()

        // Add legend
        builder.appendLine("## Legend")
        builder.appendLine()
        builder.appendLine("- **App Layer** (Yellow): Main application module")
        builder.appendLine("- **Feature Modules** (Green): User-facing features with UI")
        builder.appendLine("- **Core Modules** (Blue): Shared infrastructure and business logic")
        builder.appendLine()

        // Add module descriptions
        builder.appendLine("## Module Descriptions")
        builder.appendLine()

        if (appModules.isNotEmpty()) {
            builder.appendLine("### Application Layer")
            builder.appendLine("- **composeApp**: Main application module with navigation and DI setup")
            builder.appendLine()
        }

        if (featureModules.isNotEmpty()) {
            builder.appendLine("### Feature Modules")
            builder.appendLine("- **movies**: Movies list and details screens")
            builder.appendLine("- **tv-shows**: TV shows list and details screens")
            builder.appendLine("- **search**: Multi-type search functionality")
            builder.appendLine("- **profile**: User profile and settings")
            builder.appendLine("- **person**: Cast/crew details screen")
            builder.appendLine()
        }

        if (coreModules.isNotEmpty()) {
            builder.appendLine("### Core Modules")
            builder.appendLine("- **data**: Repository implementations")
            builder.appendLine("- **ui**: Shared UI components and theme")
            builder.appendLine("- **network**: HTTP client and API definitions")
            builder.appendLine("- **database**: Room database for offline storage")
            builder.appendLine("- **datastore**: Preferences and state persistence")
            builder.appendLine("- **model**: Domain models and DTOs")
            builder.appendLine("- **common**: Common utilities and dispatchers")
        }

        return builder.toString()
    }

    private fun formatModuleName(path: String): String {
        return path.removePrefix(":").split(":").last()
    }

    private fun formatNodeId(path: String): String {
        return path.removePrefix(":")
            .replace(":", "")
            .replace("-", "")
    }
}
