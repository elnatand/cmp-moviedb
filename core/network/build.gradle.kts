import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization) // for remote objects
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.core.network"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
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

        if (tmdbApiKey.isEmpty()) {
            logger.warn("WARNING: TMDB_API_KEY is not set. API calls will fail at runtime.")
            logger.warn("Please create secrets.properties with TMDB_API_KEY=<your-key>")
        }

        buildConfigField(
            FieldSpec.Type.STRING,
            "TMDB_API_KEY",
            tmdbApiKey
        )
    }
}
