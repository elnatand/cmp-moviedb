plugins {
    id("moviedb.kotlin.multiplatform")
}

kotlin {

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)

            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
        }
    }
}
