enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "CMPMovieDB"
include(":composeApp")
include(":core:common")
include(":core:data")
include(":core:network")
include(":core:model")
include(":core:ui")
include(":core:database")
include(":features:movies")
include(":features:tv-shows")
include(":features:profile")
include(":features:search")
