rootProject.name = "RevestCatalog"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// ── App ────────────────────────────────────────────────────
include(":androidApp")

// ── Core modules ───────────────────────────────────────────
include(":core:common")
include(":core:network")
include(":core:security")
include(":core:navigation")
include(":core:ui")

// ── Domain ─────────────────────────────────────────────────
include(":domain")

// ── Data ───────────────────────────────────────────────────
include(":data")

// ── Feature modules ────────────────────────────────────────
include(":feature:splash")
include(":feature:productlist")
include(":feature:productdetail")
