package com.revest.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// All color references come from Color.kt — no raw hex in this file

private val LightScheme = lightColorScheme(
    primary              = PrimaryLight,
    onPrimary            = OnPrimaryLight,
    primaryContainer     = PrimaryContainerLight,
    onPrimaryContainer   = OnPrimaryContainerLight,
    secondary            = SecondaryLight,
    onSecondary          = OnSecondaryLight,
    secondaryContainer   = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary             = TertiaryLight,
    tertiaryContainer    = TertiaryContainerLight,
    error                = ErrorLight,
    errorContainer       = ErrorContainerLight,
    onError              = OnErrorLight,
    onErrorContainer     = OnErrorContainerLight,
    surface              = SurfaceLight,
    onSurface            = OnSurfaceLight,
    surfaceVariant       = SurfaceVariantLight,
    outline              = OutlineLight
)

private val DarkScheme = darkColorScheme(
    primary              = PrimaryDark,
    onPrimary            = OnPrimaryDark,
    primaryContainer     = PrimaryContainerDark,
    onPrimaryContainer   = OnPrimaryContainerDark,
    secondary            = SecondaryDark,
    onSecondary          = OnSecondaryDark,
    tertiary             = TertiaryDark,
    surface              = SurfaceDark,
    onSurface            = OnSurfaceDark,
    surfaceVariant       = SurfaceVariantDark,
    outline              = OutlineDark,
    error                = ErrorDark,
    onError              = OnErrorDark,
    errorContainer       = ErrorContainerDark,
    onErrorContainer     = OnErrorContainerDark
)

@Composable
fun RevestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        content     = content
    )
}