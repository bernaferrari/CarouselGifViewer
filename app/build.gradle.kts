import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidLibrary {
        namespace = "com.bernaferrari.carouselgifviewer.library"
        compileSdk = 36
        minSdk = 24

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    val iosTargets = listOf(
        iosArm64(),
        iosSimulatorArm64()
    )

    iosTargets.forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            binaryOption("bundleId", "com.bernaferrari.carouselgifviewer")
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.resources)
                implementation(libs.compose.ui.tooling.preview)

                implementation(libs.navigation3.ui)
                implementation(libs.lifecycle.viewmodel.navigation3)
                implementation(libs.lifecycle.runtime.compose)
                implementation(libs.compose.material.icons.core)
                implementation(libs.compose.material.icons.extended)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation(libs.coil.compose)

                implementation(libs.koin.core)
                implementation(libs.koin.compose.viewmodel)

                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.mediaplayer.kmp)
                implementation(libs.datastore.preferences)
            }
        }
        
        val nonAndroidMain by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.media3.exoplayer)
                implementation(libs.media3.datasource)
                implementation(libs.media3.ui)
                implementation(libs.kotlinx.coroutines.android)
            }
        }

        val iosMain by creating {
            dependsOn(nonAndroidMain)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        iosTargets.forEach { target ->
            target.compilations.getByName("main").defaultSourceSet.dependsOn(iosMain)
        }
    }
}