package com.example.photoclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.photoclone.presentation.navigation.PhotoCloneNavigation
import com.example.photoclone.presentation.theme.PhotoCloneTheme

/**
 *  Copyright 2024 The Android Open Source Project
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  MainActivity.kt - The main entry point of the app, setting up the Compose content and theme.
 *  This file defines the MainActivity class which extends ComponentActivity. In the onCreate method, it calls setContent to set the Compose UI content for the activity. The PhotoCloneTheme composable is used to apply the app's theme, and within it, a Surface is defined that fills the maximum size of the screen and uses the background color from the theme. Inside this Surface, the PhotoCloneApp composable is called, which serves as the top-level composable for the app's UI and navigation.
 *  *  The PhotoCloneApp composable is a simple wrapper that calls the PhotoCloneNavigation composable, which sets up the navigation host and defines the app's navigation graph. This structure allows for a clean separation of concerns, with MainActivity handling the overall app setup and PhotoCloneApp managing the navigation and UI content.
 *  *  A preview composable, PhotoCloneAppPreview, is also defined to allow
 *  for easy previewing of the app's UI in Android Studio/IDE previews. It applies the same theme and calls PhotoCloneApp to render the UI in the preview.
 *  Overall, this file serves as the main entry point for the app, setting up the Compose content and theme, and providing a structure for the app's navigation and UI rendering.
 *
 * */
// Main entry activity: sets the Compose content and theme for the app.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhotoCloneApp() // top-level composable hosting app navigation
                }
            }
        }
    }
}

// Top-level app composable which starts the navigation host.
@Composable
fun PhotoCloneApp() {
    PhotoCloneNavigation()
}

// Preview that renders the app shell in Android Studio/IDE previews.
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PhotoCloneAppPreview() {
    PhotoCloneTheme {
        PhotoCloneApp()
    }
}