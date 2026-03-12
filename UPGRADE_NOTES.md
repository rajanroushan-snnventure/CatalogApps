# Upgrade: Gradle 8.14.1 + Coil 3 iOS Fix

## Changes in This Version

| Component | Before | After |
|-----------|--------|-------|
| Gradle | 8.7 (pinned workaround) | 8.14.1 |
| AGP | 8.3.2 | 8.5.2 |
| Kotlin | 2.0.0 | 2.0.21 |
| Coil | 3.0.4 (broken on iOS) | 3.0.4 (fixed) |

---

## Fix 1: getDependencyProject() — Gradle 8.7 Pin Removed

### The Error
```
Caused by: java.lang.NoSuchMethodError:
  'org.gradle.api.Project org.gradle.api.artifacts.ProjectDependency.getDependencyProject()'
    at org.jetbrains.kotlin.gradle.plugin.mpp.ModuleIds.fromDependency(ModuleIds.kt:21)
```

### Root Cause
Gradle 8.8+ removed `getDependencyProject()`. KGP 2.0.0 called it internally.
The previous workaround was to pin Gradle to 8.7 (last version with the method).

### Proper Fix (Applied)
Upgrade **Kotlin to 2.0.21** — KGP 2.0.21 no longer calls `getDependencyProject()`,
so Gradle 8.14.1 works without any workarounds.

---

## Fix 2: Coil 3.x Images Not Loading on iOS

### The Error
Images load on Android but show blank/error on iOS. Console shows:
```
No NetworkFetcher found. Add coil-network-ktor or coil-network-okhttp to your dependencies.
```
Or a crash at startup if no `ImageLoader` is registered.

### Root Cause — Three Separate Issues

**Issue A — Missing Ktor Darwin Engine**
`coil-network-ktor` is a multiplatform artifact. It ships the `KtorNetworkFetcherFactory`
but does NOT bundle any specific Ktor engine. On Android, Ktor uses OkHttp by default.
On iOS, you must explicitly add `ktor-client-darwin` (the URLSession-based engine).

**Issue B — No `SingletonImageLoader` on iOS**
On Android, Coil 3 auto-initialises its `SingletonImageLoader` via the `Application`
context through `ContentProvider`. iOS has no `Application`, so you must call
`SingletonImageLoader.setSafe { ... }` manually before any `AsyncImage` composable runs.

**Issue C — Missing `core_ui` Swift import**
The Xcode target must import the `core_ui` framework (or whatever framework exposes
`CoilInitializerKt`) so Swift can call `doInitCoil()`.

### Fix Applied

#### `core/ui/build.gradle.kts`
- Added `iosMain` source set with `ktor-client-darwin` dependency
- Added `ktor-client-android` to `androidMain`
- Added `ktor-client-core` to `commonMain`

#### `core/ui/src/iosMain/kotlin/.../CoilInitializer.kt` (new file)
```kotlin
fun initCoil() {
    SingletonImageLoader.setSafe {
        ImageLoader.Builder(UIApplication.sharedApplication)
            .components {
                add(KtorNetworkFetcherFactory(httpClient = { HttpClient(Darwin) }))
            }
            .build()
    }
}
```

#### `iosApp/iosApp/RevestCatalogApp.swift`
```swift
init() {
    KoinInitializerKt.doInitKoin()
    CoilInitializerKt.doInitCoil()  // <-- added
}
```

---

## After Applying These Fixes

1. Delete `.gradle/` and all `build/` directories
2. In Android Studio: **File → Invalidate Caches → Invalidate and Restart**
3. Sync Gradle — should succeed with Gradle 8.14.1
4. Build and run iOS target — images should load via URLSession/Darwin engine

## Version Compatibility Matrix
```
Gradle   8.14.1   latest stable 8.x
AGP      8.5.2    requires Gradle >= 8.7
Kotlin   2.0.21   KGP fixed getDependencyProject; supports Compose 1.6.11
Coil     3.0.4    stable GA; requires explicit engine per platform
Ktor     2.3.12   Darwin engine compatible with Coil 3 KtorNetworkFetcherFactory
```

---

## Fix 3: `coil-network-ktor` Artifact Not Found (Build Error)

### The Error
```
Could not find io.coil-kt.coil3:coil-network-ktor:3.0.4.
Searched in:
  - https://repo.maven.apache.org/maven2/io/coil-kt/coil3/coil-network-ktor/3.0.4/
```

### Root Cause
`coil-network-ktor` **only existed during alpha** (last published version: `3.0.0-alpha08`).
When Coil 3 went stable, the artifact was renamed to distinguish Ktor versions:

| Artifact | Ktor version | Last Coil version |
|----------|-------------|-------------------|
| `coil-network-ktor` | Ktor 2.x (alpha era) | `3.0.0-alpha08` — **GONE** |
| `coil-network-ktor2` | **Ktor 2.x** | `3.0.4` ✅ |
| `coil-network-ktor3` | Ktor 3.x | `3.4.0+` |

Since this project uses **Ktor 2.3.12**, the correct artifact is **`coil-network-ktor2`**.

### Fix Applied

**`gradle/libs.versions.toml`**
```toml
# Before (broken — artifact does not exist at stable versions):
coil-network-ktor  = { module = "io.coil-kt.coil3:coil-network-ktor",  version.ref = "coil" }

# After (correct):
coil-network-ktor2 = { module = "io.coil-kt.coil3:coil-network-ktor2", version.ref = "coil" }
```

**`core/ui/build.gradle.kts`**
```kotlin
// Before:
implementation(libs.coil.network.ktor)
// After:
implementation(libs.coil.network.ktor2)
```

**`core/ui/src/iosMain/.../CoilInitializer.kt`**
```kotlin
// Before:
import coil3.network.ktor3.KtorNetworkFetcherFactory
// After:
import coil3.network.ktor2.KtorNetworkFetcherFactory
```
