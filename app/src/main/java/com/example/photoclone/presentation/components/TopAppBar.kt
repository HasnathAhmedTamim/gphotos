package com.example.photoclone.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.photoclone.R

/**
 * Component for the top app bar in the Google Photos clone. It includes the app logo on the left,
 * and action icons (add, notifications, profile) on the right. The colors and typography are designed to match the Google Photos style, and it adapts to light/dark themes. Each icon has a callback for user interaction, allowing the parent screen to handle navigation or actions when the user taps on them.
 * The top app bar is a key part of the UI, providing access to important features and reinforcing the app's branding with the logo. The use of vector icons and theming ensures that it looks good on different screen sizes and in different modes (light/dark). The layout is designed to be clean and functional, similar to the real Google Photos app.
 * */

// Top app bar with logo and action icons (add, notifications, profile).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoTopAppBar(
    onAddClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App logo (PNG) shown at the start of the top bar
                Image(
                    painter = painterResource(R.drawable.google_photos_logo),
                    contentDescription = "Google Photos Logo",
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        },
        actions = {
            // Add button (primary action)
            IconButton(
                onClick = onAddClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Notifications button
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Profile button (avatar icon)
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        // Colors adapt to light/dark mode
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
    )
}