package com.example.photoclone.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.photoclone.R

@Composable
fun PhotoGridItem(
    imageUrl: String,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 0,
    showPlaceholder: Boolean = true
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(cornerRadius.dp)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            placeholder = if (showPlaceholder) {
                painterResource(R.drawable.ic_photo_placeholder)
            } else null,
            error = painterResource(R.drawable.ic_broken_image)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectablePhotoGridItem(
    imageUrl: String,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
    ) {
        // Photo image
        AsyncImage(
            model = imageUrl,
            contentDescription = "Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            placeholder = painterResource(R.drawable.ic_photo_placeholder),
            error = painterResource(R.drawable.ic_broken_image)
        )

        // Selection overlay
        if (isSelectionMode) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                }
            ) {}

            // Checkbox indicator
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = if (isSelected) "Selected" else "Not selected",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }
            )
        }
    }
}

