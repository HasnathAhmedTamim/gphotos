package com.example.photoclone.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Google Photos Color Palette (2025-2026)
 * Matches official Material Design and Google Photos specs
 */

// Light Theme Colors
object LightColors {
    val Background = Color(0xFFFFFFFF)
    val Primary = Color(0xFF1A73E8)  // Google Blue
    val Surface = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF202124)
    val SecondaryText = Color(0xFF5F6368)
    val Divider = Color(0xFFE0E0E0)
    val IconUnselected = Color(0xFF5F6368)
    val IconSelected = Color(0xFF1A73E8)
}

// Dark Theme Colors
object DarkColors {
    val Background = Color(0xFF121212)
    val Primary = Color(0xFF8AB4F8)  // Google Blue 80
    val Surface = Color(0xFF1E1E1E)
    val OnSurface = Color(0xFFE8EAED)
    val SecondaryText = Color(0xFFBDC1C6)
    val Divider = Color(0xFF303134)
    val IconUnselected = Color(0xFFBDC1C6)
    val IconSelected = Color(0xFF8AB4F8)
}

// Common Colors
object CommonColors {
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Transparent = Color.Transparent
    val SelectionOverlay = Color(0x80000000)  // 50% black
}
