import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization) // for remote objects
    alias(libs.plugins.buildkonfig)
}

kotlin {
    android {
        namespace = "com.elna.moviedb.core.network"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        // Opt in to host-side (JVM) unit tests so commonTest runs under `./gradlew test`,
        // not only on the iOS sim target. Disabled by default in AGP 9's KMP library plugin.
        withHostTest { }
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

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
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

        // Network request/response logging. OFF by default so release builds never
        // log the request URL, which carries the TMDB api_key as a query parameter.
        // Enable locally with: ./gradlew ... -PenableNetworkLogging=true
        val enableNetworkLogging =
            (project.findProperty("enableNetworkLogging") as? String)?.toBoolean() ?: false
        buildConfigField(
            FieldSpec.Type.BOOLEAN,
            "ENABLE_NETWORK_LOGGING",
            enableNetworkLogging.toString()
        )
    }
}
