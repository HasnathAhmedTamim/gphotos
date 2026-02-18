package com.example.photoclone.presentation.theme

import androidx.compose.ui.graphics.Color

// Google Photos Primary Colors (Exact Match)
val PhotosBlue = Color(0xFF1A73E8)           // Exact Google Photos blue (light mode)
val PhotosBlueDark = Color(0xFF1557B0)       // Darker variant
val PhotosBlueLightMode = Color(0xFF8AB4F8)  // Light blue for dark mode primary
val PhotosRed = Color(0xFFEA4335)
val PhotosYellow = Color(0xFFFBBC04)
val PhotosGreen = Color(0xFF34A853)

// Light Theme Colors (Official Google Photos Spec + UX Improvements)
val BackgroundLight = Color(0xFFF5F5F5)       // Slightly gray background for contrast
val SurfaceLight = Color(0xFFFFFFFF)          // Pure white for TopAppBar (creates separation)
val SurfaceVariantLight = Color(0xFFF1F3F4)   // Exact match
val OnSurfaceLight = Color(0xFF202124)
val SecondaryTextLight = Color(0xFF5F6368)

// Dark Theme Colors (Official Google Photos Spec)
val BackgroundDark = Color(0xFF202124)        // NOT pure black! Dark gray for OLED
val SurfaceDark = Color(0xFF292A2D)           // Exact match
val SurfaceVariantDark = Color(0xFF3C4043)    // Exact match
val OnSurfaceDark = Color(0xFFE8EAED)
val SecondaryTextDark = Color(0xFF9AA0A6)

// Common Colors
val Divider = Color(0xFFDADCE0)
val OutlineLight = Color(0xFFDADCE0)
val OutlineVariantLight = Color(0xFFE8EAED)
val OutlineDark = Color(0xFF5F6368)
val OutlineVariantDark = Color(0xFF3C4043)
