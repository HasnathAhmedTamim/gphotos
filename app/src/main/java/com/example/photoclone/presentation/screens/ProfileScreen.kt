package com.example.photoclone.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Google Photos-style Profile Screen
 * Full-screen destination with account info, storage, and settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            ProfileTopAppBar(
                onBackClick = { navController.navigateUp() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(bottom = 24.dp)
        ) {
            // Profile Header
            ProfileHeader(
                userName = "Hasnath Ahmed Tamim",
                profileImageUrl = null, // TODO: Replace with actual image URL
                onManageAccountClick = { /* TODO: Navigate to account settings */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Storage Information Card
            StorageCard(
                usedStorageGB = 8.5f,
                totalStorageGB = 15f,
                onManageStorageClick = { /* TODO: Navigate to storage */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Section
            SettingsSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Links
            FooterLinks()
        }
    }
}

/**
 * Top App Bar with back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopAppBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                "Account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

/**
 * Profile Header with circular avatar, greeting, and manage account button
 */
@Composable
private fun ProfileHeader(
    userName: String,
    profileImageUrl: String?,
    onManageAccountClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image (Circular)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUrl != null) {
                // TODO: Load image with Coil
                // AsyncImage(model = profileImageUrl, contentDescription = "Profile")
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Greeting Text
        Text(
            text = "Hi, $userName!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email or Account Type
        Text(
            text = "hasnath@gmail.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Manage Account Button
        OutlinedButton(
            onClick = onManageAccountClick,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .height(40.dp)
        ) {
            Text("Manage your Google Account")
        }
    }
}

/**
 * Storage Information Card
 */
@Composable
private fun StorageCard(
    usedStorageGB: Float,
    totalStorageGB: Float,
    onManageStorageClick: () -> Unit
) {
    val storagePercentage = (usedStorageGB / totalStorageGB)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Storage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                TextButton(onClick = onManageStorageClick) {
                    Text("Manage")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Storage Progress Bar
            LinearProgressIndicator(
                progress = storagePercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.small),
                color = if (storagePercentage > 0.9f)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Storage Text
            Text(
                text = "${"%.1f".format(usedStorageGB)} GB of ${"%.0f".format(totalStorageGB)} GB used",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (storagePercentage > 0.8f) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You're running out of storage",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Settings Section with multiple items
 */
@Composable
private fun SettingsSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        SettingsItem(
            icon = Icons.Outlined.CloudDone,
            title = "Backup",
            subtitle = "Backup is on",
            onClick = { /* TODO: Open backup settings */ }
        )

        SettingsItem(
            icon = Icons.Outlined.Settings,
            title = "Photos settings",
            subtitle = null,
            onClick = { /* TODO: Open photos settings */ }
        )

        SettingsItem(
            icon = Icons.Outlined.HelpOutline,
            title = "Help & feedback",
            subtitle = null,
            onClick = { /* TODO: Open help */ }
        )

        SettingsItem(
            icon = Icons.Outlined.Info,
            title = "About Photos",
            subtitle = "Version 6.70.0.550487283",
            onClick = { /* TODO: Show about */ }
        )
    }
}

/**
 * Individual Settings Item
 */
@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Footer with Privacy Policy and Terms of Service
 */
@Composable
private fun FooterLinks() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { /* TODO: Open privacy policy */ }) {
                Text(
                    "Privacy Policy",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "•",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(onClick = { /* TODO: Open terms */ }) {
                Text(
                    "Terms of Service",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "© ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} PhotoClone",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
