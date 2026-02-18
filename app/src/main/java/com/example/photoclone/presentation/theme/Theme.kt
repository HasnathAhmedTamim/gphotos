package com.example.photoclone.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Google Photos Color Scheme - Exact Match
 * Based on official Google Photos Material Design 3 specifications
 */

private val DarkColorScheme = darkColorScheme(
    primary = PhotosBlueLightMode,           // #8AB4F8 - Lighter blue for dark mode!
    onPrimary = Color(0xFF041E49),          // Dark blue on light blue
    primaryContainer = PhotosBlueDark,
    onPrimaryContainer = Color(0xFFD8E3FF),
    secondary = PhotosGreen,
    onSecondary = Color.White,
    tertiary = PhotosRed,
    onTertiary = Color.White,
    background = BackgroundDark,             // #202124 - NOT pure black!
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,                   // #292A2D
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,     // #3C4043
    onSurfaceVariant = SecondaryTextDark,
    outline = OutlineDark,                   // #5F6368
    outlineVariant = OutlineVariantDark      // #3C4043
)

private val LightColorScheme = lightColorScheme(
    primary = PhotosBlue,                    // #1A73E8 - Exact match
    onPrimary = Color.White,
    primaryContainer = PhotosBlueDark,
    onPrimaryContainer = Color.White,
    secondary = PhotosGreen,
    onSecondary = Color.White,
    tertiary = PhotosRed,
    onTertiary = Color.White,
    background = BackgroundLight,            // #FFFFFF
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,                  // #FFFFFF (same as background)
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,    // #F1F3F4
    onSurfaceVariant = SecondaryTextLight,
    outline = OutlineLight,                  // #DADCE0
    outlineVariant = OutlineVariantLight     // #E8EAED
)

@Composable
fun PhotoCloneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

