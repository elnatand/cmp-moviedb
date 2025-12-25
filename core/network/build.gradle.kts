import java.util.Properties

plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization) // for remote objects
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.core.network"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.common)

            implementation(libs.koin.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

buildkonfig {
    packageName = "com.elna.moviedb.core.network"
    objectName = "BuildKonfig"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        val secretsPropertiesFile = rootProject.file("secrets.properties")
        val tmdbApiKey = if (secretsPropertiesFile.exists()) {
            val properties = Properties()
            secretsPropertiesFile.inputStream().use { properties.load(it) }
            properties.getProperty("TMDB_API_KEY", "")
        } else {
            ""
        }

        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "TMDB_API_KEY", tmdbApiKey)
    }
}
