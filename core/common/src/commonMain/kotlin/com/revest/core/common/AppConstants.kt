package com.revest.core.common

// ═══════════════════════════════════════════════════════════════════════
// APP CONSTANTS
// Non-security, non-UI constants shared across the app.
// Security/network constants live in SecurityConfig (core:security).
// UI strings live in strings.xml (Android) or Strings.kt (iOS shared).
// ═══════════════════════════════════════════════════════════════════════

object AppConstants {

    // ── Pagination ───────────────────────────────────────────────────
    const val PAGE_SIZE = 20

    // ── Infinite-scroll trigger ──────────────────────────────────────
    /** Load next page when this many items remain before the list end */
    const val LOAD_MORE_THRESHOLD = 4

    // ── Search ───────────────────────────────────────────────────────
    /** Debounce delay in ms before a search query is fired */
    const val SEARCH_DEBOUNCE_MS = 400L

    // ── Splash ───────────────────────────────────────────────────────
    const val SPLASH_DURATION_MS         = 2_200L
    const val SPLASH_FADE_IN_MS          = 600
    const val SPLASH_TEXT_FADE_DELAY_MS  = 200L
    const val SPLASH_TEXT_FADE_IN_MS     = 500

    // ── App versioning ───────────────────────────────────────────────
    const val APP_VERSION = "v1.0.0"

    // ── Secure storage keys ──────────────────────────────────────────
    // Kept here (not SecurityConfig) because these are storage KEY names,
    // not security policy — any module may need to read auth tokens.
    const val KEY_AUTH_TOKEN    = "auth_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_USER_ID       = "user_id"

    // ── Deep-link scheme ─────────────────────────────────────────────
    // Duplicated in AppRoute.SCHEME — keep in sync or reference from there.
    const val DEEP_LINK_SCHEME = "revest://catalog"
}