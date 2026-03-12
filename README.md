# 🛍️ Revest Catalog — Multi-Modular KMM App

Production-grade **Kotlin Multiplatform Mobile** product catalog app built with:
- **Compose Multiplatform** shared UI (Android + iOS)
- **Multi-module Clean Architecture**
- **Koin** dependency injection
- **Ktor** networking with security hardening
- **Navigation + Deep Links** in a dedicated core module
- **Animated Splash Screen** (Android 12 SplashScreen API + Compose)
- **Certificate Pinning** + **Network Security Config**

API: [DummyJSON Products API](https://dummyjson.com/docs/products)

---

## 📐 Module Dependency Graph

```
androidApp
    ├── feature:splash
    ├── feature:productlist  ─────────────┐
    ├── feature:productdetail ────────────┤
    │                                     ↓
    ├── core:navigation            domain (UseCases, Models, Repo interface)
    ├── core:ui                          ↑
    ├── core:network ──────────── data (ProductApiService, RepositoryImpl)
    │       ↑                           ↑
    └── core:security          core:network
            ↑
        core:common (BaseViewModel, Resource, AppDispatchers)
```

### Module responsibilities

| Module | Layer | Responsibility |
|---|---|---|
| `core:common` | Foundation | `BaseViewModel`, `StateDelegate`, `Resource<T>`, `AppDispatchers` |
| `core:security` | Security | `SecurityConfig`, `SecureStorage`, Android cert-pinning OkHttpClient |
| `core:network` | Network | Ktor `HttpClient` factory (timeout, retry, auth injection, logging) |
| `core:navigation` | Navigation | `AppRoute` sealed class, `Navigator` interface, deep-link URIs |
| `core:ui` | UI | `RevestTheme` (Material 3), shared `ProductCard`, `SearchBar`, `ErrorView` |
| `domain` | Domain | `Product`, `ProductsPage` models, `ProductRepository` interface, 5 use cases |
| `data` | Data | `ProductApiService` (Ktor), `ProductRepositoryImpl`, `ProductDto` + mappers |
| `feature:splash` | Presentation | Animated Compose splash screen + OS SplashScreen API integration |
| `feature:productlist` | Presentation | Product list, search, category filter, pagination, `ProductListViewModel` |
| `feature:productdetail` | Presentation | Product detail, hero image, rating, add-to-cart, `ProductDetailViewModel` |
| `androidApp` | App | `MainActivity`, `AppNavHost`, `RevestApplication` (Koin init), deep-link resolver |

---

## 🗂️ Full File Structure

```
RevestCatalog/
├── androidApp/
│   ├── src/main/
│   │   ├── kotlin/com/revest/catalog/
│   │   │   ├── MainActivity.kt          # SplashScreen API + deep-link resolver
│   │   │   ├── AppNavHost.kt            # NavHost + deep links (Compose Navigation)
│   │   │   ├── RevestApplication.kt     # Application class, Koin startKoin()
│   │   │   └── BuildConfig.kt
│   │   ├── res/
│   │   │   ├── drawable/ic_splash_logo.xml
│   │   │   ├── values/themes.xml        # Splash screen theme
│   │   │   ├── values/strings.xml
│   │   │   └── xml/network_security_config.xml  # HTTPS-only + cert pins
│   │   └── AndroidManifest.xml          # Deep-link intent filters
│   └── proguard-rules.pro
│
├── core/
│   ├── common/   BaseViewModel · StateDelegate · Resource · AppDispatchers
│   ├── security/ SecurityConfig · SecureStorage · AndroidSecurityModule (OkHttp cert-pin)
│   ├── network/  HttpClientFactory (Ktor: timeout/retry/auth) · NetworkModule
│   ├── navigation/ AppRoute · Navigator · AppNavigator (SharedFlow commands)
│   └── ui/       RevestTheme (M3) · ProductCard · SearchBar · ErrorView · EmptyState
│
├── domain/
│   ├── model/    Product · ProductsPage
│   ├── repository/ ProductRepository (interface)
│   ├── usecase/  GetProductsUseCase · SearchProductsUseCase · GetProductDetailUseCase
│   │             GetCategoriesUseCase · GetProductsByCategoryUseCase
│   └── DomainModule.kt   ← Koin
│
├── data/
│   ├── remote/   ProductDto · ProductApiService · Mappers
│   ├── repository/ ProductRepositoryImpl
│   └── di/DataModule.kt  ← Koin
│
├── feature/
│   ├── splash/   SplashScreen.kt (animated Compose + OS splash bridge)
│   ├── productlist/
│   │   ├── presentation/ ProductListViewModel · ProductListScreen
│   │   └── di/ ProductListModule.kt
│   └── productdetail/
│       ├── presentation/ ProductDetailViewModel · ProductDetailScreen
│       └── di/ ProductDetailModule.kt
│
├── iosApp/
│   └── iosApp/  RevestCatalogApp.swift · ContentView.swift · KoinInitializer
│
└── gradle/libs.versions.toml            # Version catalog
```

---

## 🔑 Architecture Highlights

### 1. StateDelegate Pattern (Property Delegate)

```kotlin
// core:common — BaseViewModel.kt
class StateDelegate<S>(initialState: S) : ReadOnlyProperty<Any?, StateFlow<S>> {
    private val _flow = MutableStateFlow(initialState)
    val flow: StateFlow<S> = _flow.asStateFlow()

    fun update(transform: (S) -> S) { _flow.value = transform(_flow.value) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): StateFlow<S> = flow
}

// Usage in any ViewModel:
class ProductListViewModel(...) : BaseViewModel() {

    private val _stateDelegate = stateDelegate(ProductListState())
    val state: StateFlow<ProductListState> by _stateDelegate   // ← Kotlin property delegate

    fun onEvent(event: ProductListEvent) {
        _stateDelegate.update { it.copy(isLoading = true) }   // atomic update
    }
}
```

### 2. Koin DI — all modules wired in `RevestApplication`

```kotlin
startKoin {
    androidContext(this@RevestApplication)
    modules(
        securityModule,          // Settings
        androidSecurityModule,   // OkHttpClient + CertificatePinner + SecureStorage
        networkModule,           // HttpClient (Ktor, uses SecureStorage for auth token)
        dataModule,              // ProductApiService + ProductRepositoryImpl
        domainModule,            // 5 use cases
        productListModule,       // ProductListViewModel
        productDetailModule      // ProductDetailViewModel
    )
}
```

### 3. Navigation + Deep Links

```kotlin
// core:navigation — AppRoute.kt
sealed class AppRoute(val route: String) {
    object Splash        : AppRoute("splash")
    object ProductList   : AppRoute("products")
    object ProductDetail : AppRoute("product/{productId}") {
        fun createRoute(id: Int) = "product/$id"
        fun createDeepLink(id: Int) = "$SCHEME/product/$id"
    }
    companion object {
        const val SCHEME = "revest://catalog"
        fun fromDeepLink(uri: String): AppRoute? = ...
    }
}

// androidApp — AndroidManifest.xml
<intent-filter android:autoVerify="true">
    <data android:scheme="revest" android:host="catalog" android:pathPrefix="/product"/>
</intent-filter>

// Test a deep link on device:
// adb shell am start -W -a android.intent.action.VIEW \
//   -d "revest://catalog/product/1" com.revest.catalog
```

### 4. Security Layers

| Layer | Mechanism |
|---|---|
| Transport | HTTPS enforced via `network_security_config.xml` |
| Certificate Pinning | `OkHttpClient.CertificatePinner` (SHA-256 pins) + XML pins |
| Token Storage | `SecureStorage` (multiplatform-settings, swap to EncryptedSharedPreferences) |
| Auth Headers | Ktor `defaultRequest` injects `Authorization: Bearer <token>` |
| Retry Logic | Ktor `HttpRequestRetry` — 3 retries with exponential back-off |
| Release Hardening | `isMinifyEnabled=true` + ProGuard rules + `allowBackup=false` |

### 5. Splash Screen (two-layer)

| Layer | What it does |
|---|---|
| Android OS Splash | `installSplashScreen()` — zero white-flash launch (Android 12+) |
| Compose Splash | `SplashScreen.kt` — animated gradient + icon bounce + fade-in brand name |

---

## 🧪 Tests

| Test file | What's covered |
|---|---|
| `domain/UseCaseTests.kt` | `GetProducts`, `Search`, `GetDetail`, pagination, model computed props |
| `data/ProductRepositoryImplTest.kt` | Ktor `MockEngine` — success mapping, server error, search |
| `feature/productlist/ProductListViewModelTest.kt` | Init load, error state, dismiss, category selection, refresh |

Run all:
```bash
./gradlew allTests
# Windows:
gradlew.bat allTests
```

---

## 🚀 Getting Started on Windows

### Prerequisites
| Tool | Minimum version |
|---|---|
| JDK | 17 (Temurin/Corretto) |
| Android Studio | Hedgehog 2023.1.1+ |
| KMM plugin | 0.8.0+ |
| Gradle | 8.x (via wrapper) |

> iOS targets are compiled only on macOS. The Gradle property
> `kotlin.native.ignoreDisabledTargets=true` in `gradle.properties` ensures
> the Windows build skips iOS native targets without errors.

### Windows Quick Start

```powershell
# 1. Clone
git clone https://github.com/your-org/RevestCatalog.git
cd RevestCatalog

# 2. Open in Android Studio → File → Open → select folder
# OR build from command line:

# 3. Build debug APK
gradlew.bat :androidApp:assembleDebug

# 4. Install on connected device / emulator
gradlew.bat :androidApp:installDebug

# 5. Run tests
gradlew.bat :domain:allTests
gradlew.bat :data:allTests
gradlew.bat :feature:productlist:allTests

# 6. Run all tests
gradlew.bat allTests
```

### Deep link testing (Windows ADB)

```powershell
# Product List
adb shell am start -W -a android.intent.action.VIEW `
    -d "revest://catalog/products" com.revest.catalog

# Product Detail (id = 42)
adb shell am start -W -a android.intent.action.VIEW `
    -d "revest://catalog/product/42" com.revest.catalog
```

### iOS (macOS only)

```bash
# Build the shared framework
./gradlew :feature:productlist:linkDebugFrameworkIosSimulatorArm64

# Open in Xcode
open iosApp/iosApp.xcodeproj
# Then Product → Run (⌘R)
```

---

## 📦 Key Dependencies

| Library | Version | Purpose |
|---|---|---|
| Compose Multiplatform | 1.6.11 | Shared UI (Android + iOS) |
| Kotlin | 2.0.0 | Language |
| Ktor Client | 2.3.12 | HTTP networking |
| Koin | 3.6.0-alpha3 | Dependency injection |
| kotlinx.serialization | 1.6.3 | JSON parsing |
| Coil 3 | 3.0.0-alpha06 | Async image loading |
| OkHttp | 4.12.0 | Certificate pinning (Android) |
| multiplatform-settings | 1.1.1 | Secure key-value storage |
| androidx-core-splashscreen | 1.0.1 | Android 12 SplashScreen API |
| androidx-navigation-compose | 2.7.7 | Jetpack navigation + deep links |
| kotlinx-coroutines | 1.8.1 | Async + StateFlow |

---

## 🔄 Data Flow

```
User Action
    ↓
Composable → onEvent(Event)
    ↓
ViewModel (StateDelegate<State>)
    ↓
UseCase (domain)
    ↓
Repository interface (domain)
    ↓
RepositoryImpl (data)
    ↓
ProductApiService → Ktor HttpClient
    ↓ (HTTPS + cert-pin + retry + auth header)
DummyJSON REST API
    ↓
ProductDto → toDomain() → Product
    ↓
Result<ProductsPage>
    ↓
_stateDelegate.update { it.copy(products = ...) }
    ↓
StateFlow<State> → collectAsState() → Composable recomposition
```
