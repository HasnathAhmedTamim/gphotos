package com.example.photoclone.ui.theme

import androidx.compose.runtime.Composable
import com.example.photoclone.presentation.theme.PhotoCloneTheme as PresentationTheme

/**
 * Thin wrapper that re-uses the central `presentation.theme.PhotoCloneTheme` implementation.
 * This keeps callers importing `com.example.photoclone.ui.theme.PhotoCloneTheme` working
 * while consolidating the real theme implementation in `presentation.theme`.
 */
@Composable
fun PhotoCloneTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Delegate to the presentation theme which contains the canonical colors & typography
    PresentationTheme(darkTheme = darkTheme, content = content)
}