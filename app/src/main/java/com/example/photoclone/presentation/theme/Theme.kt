package com.example.photoclone.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PhotosBlue,
    onPrimary = Color.White,
    primaryContainer = PhotosBlueDark,
    onPrimaryContainer = Color.White,
    secondary = PhotosGreen,
    onSecondary = Color.White,
    tertiary = PhotosRed,
    onTertiary = Color.White,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = SecondaryTextDark,
    outline = Divider
)

private val LightColorScheme = lightColorScheme(
    primary = PhotosBlue,
    onPrimary = Color.White,
    primaryContainer = PhotosBlueDark,
    onPrimaryContainer = Color.White,
    secondary = PhotosGreen,
    onSecondary = Color.White,
    tertiary = PhotosRed,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = SecondaryTextLight,
    outline = Divider
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