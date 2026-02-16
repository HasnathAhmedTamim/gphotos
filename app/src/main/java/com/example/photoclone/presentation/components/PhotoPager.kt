package com.example.photoclone.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.photoclone.R


/**
 * Composable for a full-screen photo pager, used when a user taps on a photo to view it in detail.
 * - Displays photos one-by-one in a horizontal pager.
 * - Shows a top app bar with the current photo index and a back button to dismiss the pager.
 * - Each photo is loaded from a URL with proper scaling and placeholders for loading/error states.
 * - The pager state is remembered across recompositions, allowing for smooth navigation between photos.
 * - The onDismiss callback allows the parent composable to control when the pager is shown or hidden.
 * - The modifier parameter allows for further customization of the pager's layout and appearance.

 * */
// Full-screen horizontal pager for viewing photos one-by-one.
// Shows a top app bar with current index and a back action.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPager(
    photoUrls: List<String>, // list of remote/local image URLs
    initialPage: Int, // starting page index
    onDismiss: () -> Unit, // called when back is pressed
    modifier: Modifier = Modifier
) {
    // Pager state holds current page and pageCount
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { photoUrls.size }
    )

    // Scaffold with a top bar showing "current / total"
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Human-friendly 1-based index display
                    Text("${pagerState.currentPage + 1} / ${photoUrls.size}")
                },
                navigationIcon = {
                    // Back button dismisses the pager
                    IconButton(onClick = onDismiss) {
                        Icon(
                            // ArrowBack icon
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { padding ->
        // HorizontalPager presents each image full-screen
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // AsyncImage loads each URL with a placeholder and error drawable
                AsyncImage(
                    model = photoUrls[page],
                    contentDescription = "Photo ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit, // fit entire image inside viewport
                    placeholder = painterResource(R.drawable.ic_photo_placeholder),
                    error = painterResource(R.drawable.ic_broken_image)
                )
            }
        }
    }
}
