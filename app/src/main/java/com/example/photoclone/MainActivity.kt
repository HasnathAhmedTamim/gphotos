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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoCloneTheme {
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
fun PhotoCloneApp() {
    PhotoCloneNavigation()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PhotoCloneAppPreview() {
    PhotoCloneTheme {
        PhotoCloneApp()
    }
}