package com.revest.feature.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.revest.core.common.AppConstants
import com.revest.core.ui.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {

    val iconScale by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )
    val alpha     = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(AppConstants.SPLASH_FADE_IN_MS, easing = FastOutSlowInEasing))
        delay(AppConstants.SPLASH_TEXT_FADE_DELAY_MS)
        textAlpha.animateTo(1f, tween(AppConstants.SPLASH_TEXT_FADE_IN_MS, easing = FastOutSlowInEasing))
        delay(AppConstants.SPLASH_DURATION_MS - 800)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SplashGradientStart, SplashGradientMid, SplashGradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingXxl)
        ) {
            Surface(
                modifier       = Modifier
                    .size(SizeSplashIconBox)
                    .scale(iconScale)
                    .alpha(alpha.value),
                shape          = RoundedCornerShape(RadiusXxl),
                color          = IconContainerWhite.copy(alpha = 0.15f),
                tonalElevation = ZeroDP
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = Icons.Filled.Star,
                        contentDescription = "Revest Logo",   // → stringResource in Android wrapper
                        modifier           = Modifier.size(SizeSplashIcon),
                        tint               = IconContainerWhite
                    )
                }
            }

            Column(
                modifier            = Modifier.alpha(textAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpacingM)
            ) {
                Text(
                    text  = "REVEST",   // → stringResource in Android wrapper
                    style = MaterialTheme.typography.displaySmall.copy(
                        letterSpacing = LetterSpacingBrand,
                        fontWeight    = FontWeight.ExtraBold
                    ),
                    color = IconContainerWhite
                )
                Text(
                    text  = "Product Catalog",   // → stringResource in Android wrapper
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = LetterSpacingSubtitle
                    ),
                    color = IconContainerWhite.copy(alpha = 0.75f)
                )
            }
        }

        Text(
            text     = AppConstants.APP_VERSION,
            style    = MaterialTheme.typography.labelSmall,
            color    = IconContainerWhite.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = SplashVersionPaddingBottom)
                .alpha(textAlpha.value)
        )
    }
}