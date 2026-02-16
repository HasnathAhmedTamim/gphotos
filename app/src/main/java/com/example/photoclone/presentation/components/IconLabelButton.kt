package com.example.photoclone.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reusable circular icon button with a label underneath.
 */
@Composable
fun IconLabelButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    labelStyle: TextStyle = MaterialTheme.typography.labelSmall
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(size)
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(size),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size((size * 0.42f).coerceAtLeast(24.dp))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = labelStyle,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(size)
        )
    }
}
