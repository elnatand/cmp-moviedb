import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.buildkonfig)
}


kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.core.common"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core) // for AppDispatchers
            implementation(libs.koin.core)
        }
    }
}

buildkonfig {
    packageName = "com.elna.moviedb.core.common.utils"
    objectName = "BuildKonfig"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        val versionCatalog = project.extensions.findByType(
            VersionCatalogsExtension::class.java
        )
        val libs = versionCatalog?.find("libs")?.orElse(null)
        val appVersion = libs?.findVersion("app-version")?.orElse(null)?.requiredVersion ?: "1.0.0"

        buildConfigField(FieldSpec.Type.STRING, "APP_VERSION", appVersion)
    }
}
