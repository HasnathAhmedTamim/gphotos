package com.example.photoclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.photoclone.presentation.theme.PhotoCloneTheme

/**
 *  MainActivity - Edge-to-edge with transparent status bar for immersive Google Photos experience
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        setContent {
            PhotoCloneTheme {
                // Setup transparent system bars
                SetupSystemBars()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhotoCloneApp()
                }
            }
        }
    }
}

@Composable
private fun SetupSystemBars() {
    val view = LocalView.current
    val darkTheme = isSystemInDarkTheme()

    SideEffect {
        val window = (view.context as ComponentActivity).window

        // Transparent status bar for immersive experience
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

        // Light/dark icons based on theme
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

// Top-level app composable which starts the navigation host.
@Composable
fun PhotoCloneApp() {
    // Google Photos Style UI (Material 3)
    com.example.photoclone.presentation.navigation.GooglePhotosNavigation()
}

// Preview that renders the app shell in Android Studio/IDE previews.
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PhotoCloneAppPreview() {
    PhotoCloneTheme {
        PhotoCloneApp()
    }
}