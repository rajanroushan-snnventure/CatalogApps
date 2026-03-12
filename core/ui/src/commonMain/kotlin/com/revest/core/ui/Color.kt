package com.revest.core.ui

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════
// REVEST BRAND PALETTE
// Source of truth for every color used in the app.
// RevestTheme.kt references these; nothing else should use raw Color(0xFF...)
// ═══════════════════════════════════════════════════════════════════════

// ── Primary — Deep Indigo ────────────────────────────────────────────
val PrimaryLight            = Color(0xFF1A237E)
val OnPrimaryLight          = Color.White
val PrimaryContainerLight   = Color(0xFFDDE1FF)
val OnPrimaryContainerLight = Color(0xFF00105C)

val PrimaryDark             = Color(0xFFBAC3FF)
val OnPrimaryDark           = Color(0xFF00218A)
val PrimaryContainerDark    = Color(0xFF003399)
val OnPrimaryContainerDark  = Color(0xFFDDE1FF)

// ── Secondary ────────────────────────────────────────────────────────
val SecondaryLight              = Color(0xFF283593)
val OnSecondaryLight            = Color.White
val SecondaryContainerLight     = Color(0xFFDFE0FF)
val OnSecondaryContainerLight   = Color(0xFF000F5C)

val SecondaryDark               = Color(0xFFBBC4FF)
val OnSecondaryDark             = Color(0xFF00219A)

// ── Tertiary ─────────────────────────────────────────────────────────
val TertiaryLight           = Color(0xFF3F51B5)
val TertiaryContainerLight  = Color(0xFFE3E5FF)

val TertiaryDark            = Color(0xFFBFC6FF)

// ── Error ────────────────────────────────────────────────────────────
val ErrorLight          = Color(0xFFBA1A1A)
val OnErrorLight        = Color.White
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)

val ErrorDark           = Color(0xFFFFB4AB)
val OnErrorDark         = Color(0xFF690005)
val ErrorContainerDark  = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

// ── Surface / Background ─────────────────────────────────────────────
val SurfaceLight        = Color(0xFFFBF8FF)
val OnSurfaceLight      = Color(0xFF1B1B1F)
val SurfaceVariantLight = Color(0xFFE3E1EC)
val OutlineLight        = Color(0xFF75737D)

val SurfaceDark         = Color(0xFF131318)
val OnSurfaceDark       = Color(0xFFE5E1E9)
val SurfaceVariantDark  = Color(0xFF47464F)
val OutlineDark         = Color(0xFF908E98)

// ── Splash gradient ──────────────────────────────────────────────────
// Used only by SplashScreen — defined here so the hex lives in one place
val SplashGradientStart  = Color(0xFF0D1B6E)
val SplashGradientMid    = Color(0xFF1A237E)   // same as PrimaryLight
val SplashGradientEnd    = Color(0xFF283593)   // same as SecondaryLight

// ── Utility ──────────────────────────────────────────────────────────
val IconContainerWhite  = Color.White           // icon tint on splash
val OverlayBlack20      = Color.Black.copy(alpha = 0.20f) // hero image gradient