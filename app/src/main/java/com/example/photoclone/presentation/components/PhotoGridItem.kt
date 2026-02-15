package com.example.photoclone.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.example.photoclone.R

@Suppress("unused")
@Composable
fun PhotoGridItem(
    imageUrl: String,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 8,
    showPlaceholder: Boolean = true,
    imageRequestSizePx: Int? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(cornerRadius.dp)),
            contentAlignment = Alignment.Center
        ) {
            val context = LocalContext.current
            val request = remember(imageUrl, imageRequestSizePx) {
                val builder = ImageRequest.Builder(context).data(imageUrl).crossfade(true)
                imageRequestSizePx?.let { builder.size(Size(it, it)) }
                builder.allowHardware(true)
                builder.build()
            }

            AsyncImage(
                model = request,
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
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectablePhotoGridItem(
    imageUrl: String,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 8,
    imageRequestSizePx: Int? = null
) {
    // Press-state interaction for subtle scale feedback
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f)

    // Animated alpha for overlay: stronger when selected, subtle tint when in selection mode
    val targetOverlayAlpha = when {
        isSelected -> 0.35f
        isSelectionMode -> 0.06f
        else -> 0f
    }
    val overlayAlpha by animateFloatAsState(targetValue = targetOverlayAlpha)

    val haptic = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            // Scale the whole item slightly on press to communicate affordance
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = {
                    // Haptic feedback for long press
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onLongPress()
                }
            )
            .semantics {
                contentDescription = "Photo"
                stateDescription = if (isSelected) "selected" else "not selected"
            },
        shape = RoundedCornerShape(cornerRadius.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 4.dp)
    ) {
        Box {
            // Photo image
            val context = LocalContext.current
            val request = remember(imageUrl, imageRequestSizePx) {
                val builder = ImageRequest.Builder(context).data(imageUrl).crossfade(true)
                imageRequestSizePx?.let { builder.size(Size(it, it)) }
                builder.allowHardware(true)
                builder.build()
            }

            AsyncImage(
                model = request,
                contentDescription = "Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(R.drawable.ic_photo_placeholder),
                error = painterResource(R.drawable.ic_broken_image)
            )

            // Selection overlay (animated alpha)
            if (overlayAlpha > 0f) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = overlayAlpha)
                ) {}
            }

            // Checkbox indicator (fade/scale in when in selection mode)
            androidx.compose.animation.AnimatedVisibility(
                visible = isSelectionMode,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
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
                        // dimmed when not selected
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
            }
        }
    }
}
