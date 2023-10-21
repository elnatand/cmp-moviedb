buildscript {
    dependencies {
        classpath(libs.com.squareup.sqldelight.gradle.plugin)
        classpath(libs.dev.icerock.moko.resources.generator.plugin)
    }
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.org.jetbrains.kotlin.multiplatform.plugin) apply false
    alias(libs.plugins.com.android.application.plugin) apply false
    alias(libs.plugins.com.android.library.plugin) apply false
    alias(libs.plugins.org.jetbrains.compose.plugin) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}