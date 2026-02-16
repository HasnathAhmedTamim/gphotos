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
import androidx.compose.ui.res.stringResource
import com.example.photoclone.R


/**
 * Composable for a full-screen photo pager, used when a user taps on a photo to view it in detail.
 */
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
                            contentDescription = stringResource(R.string.back)
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
                PhotoImage(
                    imageUrl = photoUrls[page],
                    contentDescription = stringResource(R.string.photo_index_description, page + 1, photoUrls.size),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    requestSizePx = null,
                    showPlaceholder = true
                )
            }
        }
    }
}
