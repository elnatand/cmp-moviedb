
plugins {
    alias(libs.plugins.moviedb.kotlinMultiplatform)

    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    androidLibrary {
        namespace = "com.elna.moviedb.core.database"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(projects.core.model)
            implementation(projects.core.common)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.room.sqlite.wrapper)
            }
        }
    }
}

dependencies{
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
