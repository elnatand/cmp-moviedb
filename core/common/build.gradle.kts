import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)
    alias(libs.plugins.buildkonfig)
}


kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.core.common"
        compileSdk = 36
        minSdk = 24

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
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
        val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
        val appVersion = libs.findVersion("app-version").get().requiredVersion

        buildConfigField(FieldSpec.Type.STRING, "APP_VERSION", appVersion)
    }
}
