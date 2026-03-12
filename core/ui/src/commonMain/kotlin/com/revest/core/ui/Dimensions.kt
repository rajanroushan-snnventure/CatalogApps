package com.revest.core.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════════════════════════════
// DIMENSIONS
// Every dp and sp value used across the UI in one place.
// Components import these names instead of scattering magic numbers.
// ═══════════════════════════════════════════════════════════════════════

// ── Corner radii ─────────────────────────────────────────────────────
val RadiusXs    =  6.dp   // discount badge
val RadiusS     =  8.dp   // detail discount badge
val RadiusM     = 12.dp   // product card thumbnail
val RadiusL     = 16.dp   // product card
val RadiusXl    = 20.dp   // category chip on detail
val RadiusXxl   = 28.dp   // search bar, splash icon container

// ── Spacing / padding ────────────────────────────────────────────────
val SpacingXxs  =  2.dp
val SpacingXs   =  3.dp
val SpacingS    =  4.dp
val SpacingM    =  6.dp
val SpacingMl   =  8.dp
val SpacingL    = 10.dp
val SpacingXl   = 12.dp
val SpacingXxl  = 14.dp
val SpacingCard = 16.dp   // standard horizontal screen padding
val SpacingXxxl = 20.dp   // detail content padding
val Spacing2xl  = 24.dp
val Spacing3xl  = 28.dp
val Spacing4xl  = 32.dp
val Spacing5xl  = 48.dp

// ── Component sizes ──────────────────────────────────────────────────
val SizeIconXs      = 14.dp   // rating star in card
val SizeIconS       = 16.dp   // star in detail
val SizeIconM       = 18.dp   // button icon
val SizeIconL       = 20.dp   // topbar spinner
val SizeIconXl      = 28.dp   // pagination spinner
val SizeIconXxl     = 36.dp   // filter overlay spinner
val SizeIconError   = 56.dp   // empty state icon
val SizeIconWarning = 72.dp   // error view icon

val SizeCardThumbnail = 90.dp   // product card image
val SizeHeroImage     = 300.dp  // detail hero
val SizeSplashIcon    = 56.dp   // store icon on splash
val SizeSplashIconBox = 96.dp   // rounded container on splash

// ── Stroke widths ────────────────────────────────────────────────────
val StrokeS = 2.dp
val StrokeM = 3.dp
val ZeroDP = 0.dp
// ── Elevation ────────────────────────────────────────────────────────
val ElevationCard        = 2.dp
val ElevationCardPressed = 6.dp
val ElevationBottomBar   = 8.dp

// ── Button padding ───────────────────────────────────────────────────
val ButtonPaddingH  = 28.dp
val ButtonPaddingV  = 14.dp

// ── Bottom bar ───────────────────────────────────────────────────────
val BottomBarPaddingH = 20.dp
val BottomBarPaddingV = 12.dp

// ── Splash ───────────────────────────────────────────────────────────
val SplashVersionPaddingBottom = 32.dp

// ── Text (sp) ────────────────────────────────────────────────────────
val LetterSpacingBrand    = 8.sp   // "REVEST" title
val LetterSpacingSubtitle = 2.sp   // "Product Catalog"