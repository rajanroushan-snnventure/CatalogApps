import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
    iosX64(); iosArm64(); iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            // Extended icons needed for: SearchOff, Refresh, FilterList, Search, Star etc.
            implementation(compose.materialIconsExtended)
            implementation(libs.coil.compose)
            // NOTE: The artifact was renamed from coil-network-ktor to coil-network-ktor2
            // (for Ktor 2.x) after alpha08. coil-network-ktor only exists up to alpha08.
            // Use coil-network-ktor3 if you later upgrade to Ktor 3.x.
            implementation(libs.coil.network.ktor2)
            implementation(libs.ktor.client.core)
        }

        androidMain.dependencies {
            // Ktor Android engine (OkHttp-backed) — used by coil-network-ktor2 on Android
            implementation(libs.ktor.client.android)
        }

        val iosMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                // Ktor Darwin engine — required by coil-network-ktor2 on iOS.
                // Without this, Coil cannot load network images on iOS.
                implementation(libs.ktor.client.darwin)
            }
        }

        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
    }
}

android {
    namespace = "com.revest.core.ui"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
