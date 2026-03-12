package com.revest.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.revest.core.navigation.AppNavigator
import com.revest.core.navigation.AppRoute

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // ── Android 12 Splash Screen API ──────────────────────────────────
        // This shows the OS-level splash (instant, no white flash).
        // Our custom Compose splash screen plays after this completes.
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ── Deep link resolution ──────────────────────────────────────────
        val startDestination = resolveStartDestination()

        // ── Navigator ─────────────────────────────────────────────────────
        val navigator = AppNavigator()

        setContent {
            AppNavHost(
                navigator        = navigator,
                startDestination = startDestination
            )
        }
    }

    /**
     * Checks the incoming Intent URI and maps it to an [AppRoute].
     * Falls back to [AppRoute.Splash.route] for normal launches.
     */
    private fun resolveStartDestination(): String {
        val uri = intent?.data?.toString() ?: return AppRoute.Splash.route
        return when (AppRoute.fromDeepLink(uri)) {
            is AppRoute.ProductList   -> AppRoute.ProductList.route
            is AppRoute.ProductDetail -> {
                // Extract productId from path segment: /product/42
                val id = uri.substringAfterLast("/").toIntOrNull()
                if (id != null) AppRoute.ProductDetail.createRoute(id)
                else AppRoute.ProductList.route
            }
            else -> AppRoute.Splash.route
        }
    }
}
