package com.example.photoclone.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.photoclone.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPager(
    photoUrls: List<String>,
    initialPage: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { photoUrls.size }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("${pagerState.currentPage + 1} / ${photoUrls.size}")
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
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
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = photoUrls[page],
                    contentDescription = "Photo ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.ic_photo_placeholder),
                    error = painterResource(R.drawable.ic_broken_image)
                )
            }
        }
    }
}
