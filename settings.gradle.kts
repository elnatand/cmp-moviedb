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
include(":core:data")
include(":core:datastore")
include(":core:network")
include(":core:model")
include(":core:designsystem")
include(":core:navigation")
include(":core:database")

include(":features:movies:api")
include(":features:movies:impl")

include(":features:tv-shows:api")
include(":features:tv-shows:impl")

include(":features:profile:api")
include(":features:profile:impl")

include(":features:search:api")
include(":features:search:impl")

include(":features:person:api")
include(":features:person:impl")
