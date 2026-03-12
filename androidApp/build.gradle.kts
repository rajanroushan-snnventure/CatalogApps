import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
}

android {
    namespace  = "com.revest.catalog"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.revest.catalog"
        minSdk        = 24
        targetSdk     = 36
        versionCode   = 1
        versionName   = "1.0.0"

        manifestPlaceholders["deepLinkScheme"] = "revest"
        manifestPlaceholders["deepLinkHost"]   = "catalog"
    }

    // ── Source set paths ───────────────────────────────────────────────────────
    // KMP layout v2 (kotlin.mpp.androidSourceSetLayoutVersion=2) moves the
    // expected manifest location to src/androidMain/. This project keeps the
    // traditional src/main/ layout, so we declare it explicitly here to avoid
    // the "mainManifest … doesn't exist" error from processDebugMainManifest.
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            java.srcDirs("src/main/kotlin")
            res.srcDirs("src/main/res")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable        = true
        }
    }

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(projects.feature.splash)
    implementation(projects.feature.productlist)
    implementation(projects.feature.productdetail)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.security)
    implementation(projects.core.navigation)
    implementation(projects.core.ui)
    implementation(projects.data)
    implementation(projects.domain)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.androidx.core.splashscreen)
    // Theme.Material3.DayNight.NoActionBar lives in google material, not compose-material3
    implementation(libs.material)

    debugImplementation(libs.compose.ui.tooling)
}
