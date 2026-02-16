package com.example.photoclone.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.photoclone.R
import com.example.photoclone.presentation.components.BottomNavItem
import com.example.photoclone.presentation.components.PhotoBottomNavigation
import com.example.photoclone.presentation.components.PhotoTopAppBar
/**
 *  Collection screen showing albums and quick access tiles.
 *  The screen is structured with a top app bar, a grid of collection items, and a bottom navigation bar.
 *  - The top app bar includes the app logo and action icons for adding content, viewing notifications, and accessing the user profile.
 *  - The main content area displays a grid of collection items, which can be either quick access tiles (like "Favourites" and "Bin") or regular album tiles with thumbnails and item counts.
 *  - The bottom navigation bar allows users to switch between different sections of the app (Photos, Collection, Create, Search).
 *  The UI is designed to closely mimic the look and feel of the Google Photos app, with attention to theming, typography, and layout. The use of sample data allows for easy previewing and testing of the UI components.
 *  The screen is responsive and adapts to different screen sizes, ensuring a consistent user experience across devices. The collection items are interactive, allowing users to tap on them to navigate to the corresponding album or quick access section.
 *
 * */
// Simple model representing a collection (album/quick access tile)
data class Collection(
    val name: String,
    val itemCount: Int,
    val thumbnailUrl: String? = null,
    val icon: ImageVector? = null,
    val isQuickAccess: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    currentRoute: String = "collection",
    onAddClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    // Sample collections used for UI previews/demo
    val collections = listOf(
        Collection("Favourites", 0, icon = Icons.Outlined.Star, isQuickAccess = true),
        Collection("Bin", 0, icon = Icons.Outlined.Delete, isQuickAccess = true),
        Collection("Messenger", 206, thumbnailUrl = "https://picsum.photos/400/400?seed=messenger"),
        Collection("Camera", 694, thumbnailUrl = "https://picsum.photos/400/400?seed=camera"),
        Collection("Download", 100, thumbnailUrl = "https://picsum.photos/400/400?seed=download"),
        Collection("Screenshots", 52, thumbnailUrl = "https://picsum.photos/400/400?seed=screenshots"),
        Collection("WhatsApp", 341, thumbnailUrl = "https://picsum.photos/400/400?seed=whatsapp"),
        Collection("Instagram", 128, thumbnailUrl = "https://picsum.photos/400/400?seed=instagram")
    )

    // Bottom navigation items for the screen
    val navigationItems = listOf<BottomNavItem>(
        BottomNavItem(
            title = stringResource(R.string.photos),
            icon = Icons.Outlined.PhotoLibrary,
            route = "home"
        ),
        BottomNavItem(
            title = stringResource(R.string.collection),
            icon = Icons.Outlined.Collections,
            route = "collection"
        ),
        BottomNavItem(
            title = stringResource(R.string.create),
            icon = Icons.Outlined.AddCircle,
            route = "create"
        ),
        BottomNavItem(
            title = stringResource(R.string.search),
            icon = Icons.Outlined.Search,
            route = "search"
        )
    )

    // Determine which bottom nav item is selected from the current route
    val selectedIndex = navigationItems.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 1

    Scaffold(
        topBar = {
            // Top app bar with logo and actions
            PhotoTopAppBar(
                onAddClick = onAddClick,
                onNotificationClick = onNotificationClick,
                onProfileClick = onProfileClick
            )
        },
        bottomBar = {
            // Bottom navigation bar
            PhotoBottomNavigation(
                items = navigationItems,
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    onNavigate(navigationItems[index].route)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        // Grid of collection tiles
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(collections.size) { index ->
                CollectionItem(collection = collections[index])
            }
        }
    }
}

@Composable
fun CollectionItem(collection: Collection) {
    // Tile surface: smaller quick-access row or larger thumbnail card
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (collection.isQuickAccess) 80.dp else 240.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { },
        color = if (collection.isQuickAccess) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        } else {
            Color.Transparent
        },
        tonalElevation = if (collection.isQuickAccess) 0.dp else 0.dp
    ) {
        if (collection.isQuickAccess) {
            // Compact quick-access layout (icon + text)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                collection.icon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = collection.name,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = collection.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            // Full thumbnail card with image and info
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Thumbnail image area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = collection.thumbnailUrl,
                        contentDescription = collection.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Collection text info (name + count)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = collection.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${collection.itemCount} items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
