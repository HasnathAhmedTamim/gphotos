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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.photoclone.R

/**
 * Composable for a photo grid item, used in both the main collection and selection mode.
 * - Displays a photo from a URL with proper cropping and placeholders.
 * - In selection mode, shows an overlay and a check icon when selected.
 * - Pressing the item provides scale feedback, and long-pressing triggers haptic feedback
 * */

@Suppress("unused")
@Composable
fun PhotoGridItem(
    imageUrl: String,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 8,
    showPlaceholder: Boolean = true,
    imageRequestSizePx: Int? = null
) {
    // Simple, non-selectable photo tile. Shows a placeholder or error drawable when needed.
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
            PhotoImage(
                imageUrl = imageUrl,
                contentDescription = stringResource(R.string.photo_content_description),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                requestSizePx = imageRequestSizePx,
                showPlaceholder = showPlaceholder
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
    // Selectable tile with press-scaling, selection overlay, haptic feedback, and a check icon.
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

    // Compute localized strings in composable context so they can be used inside non-composable semantics builder
    val contentDesc = stringResource(R.string.photo_content_description)
    val stateDesc = if (isSelected) stringResource(R.string.photo_selected) else stringResource(R.string.photo_not_selected)

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
                contentDescription = contentDesc
                stateDescription = stateDesc
            },
        shape = RoundedCornerShape(cornerRadius.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            PhotoImage(
                imageUrl = imageUrl,
                contentDescription = contentDesc,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                requestSizePx = imageRequestSizePx,
                showPlaceholder = true
            )

            // Selection overlay (animated alpha)
            if (overlayAlpha > 0f) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = overlayAlpha)
                ) {}
            }

            // Checkbox indicator: always present but animated via alpha/scale to avoid layout churn
            val iconAlpha by animateFloatAsState(targetValue = if (isSelectionMode) 1f else 0f)
            val iconScale by animateFloatAsState(targetValue = if (isSelectionMode) 1f else 0.8f)

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stateDesc,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .scale(iconScale)
                    .graphicsLayer { alpha = iconAlpha },
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
        }
    }
}
