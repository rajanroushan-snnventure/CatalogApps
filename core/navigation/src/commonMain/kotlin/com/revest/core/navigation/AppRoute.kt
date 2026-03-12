package com.revest.core.navigation

/**
 * Type-safe route definitions for the entire app.
 *
 * Deep-link scheme: revest://catalog/<route>
 *
 * Examples:
 *   revest://catalog/products           → ProductList
 *   revest://catalog/product/42         → ProductDetail(id=42)
 *   revest://catalog/product/42?ref=push → ProductDetail with analytics ref
 */
sealed class AppRoute(val route: String) {

    // ── Splash ──────────────────────────────────────────────────────────────
    object Splash : AppRoute("splash") {
        val deepLink = "$SCHEME/splash"
    }

    // ── Product List ────────────────────────────────────────────────────────
    object ProductList : AppRoute("products") {
        val deepLink = "$SCHEME/products"
    }

    // ── Product Detail ──────────────────────────────────────────────────────
    object ProductDetail : AppRoute("product/{$ARG_PRODUCT_ID}") {
        val deepLink = "$SCHEME/product/{$ARG_PRODUCT_ID}"

        fun createRoute(productId: Int) = "product/$productId"
        fun createDeepLink(productId: Int) = "$SCHEME/product/$productId"
    }

    companion object {
        const val SCHEME = "revest://catalog"
        const val ARG_PRODUCT_ID = "productId"

        /** Resolve a raw deep-link URI to the matching [AppRoute]. */
        fun fromDeepLink(uri: String): AppRoute? = when {
            uri.endsWith("splash")         -> Splash
            uri.endsWith("products")       -> ProductList
            uri.contains("/product/")      -> ProductDetail
            else                           -> null
        }
    }
}
