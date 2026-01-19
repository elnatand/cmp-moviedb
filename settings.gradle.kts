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
include(":androidApp")
include(":composeApp")
include(":core:common")
//include(":core:data")
include(":core:datastore")
include(":core:network")
include(":core:model")
include(":core:ui")
include(":core:database")

include(":features:movies:presentation")
include(":features:movies:data")
include(":features:movies:domain")

include(":features:tv-shows:presentation")
include(":features:tv-shows:data")
include(":features:tv-shows:domain")

include(":features:search:presentation")
include(":features:search:data")
include(":features:search:domain")

include(":features:person:presentation")
include(":features:person:data")
include(":features:person:domain")

include(":features:profile:presentation")
