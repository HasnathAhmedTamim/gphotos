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
    // Sample collections with demo photos
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

    // Bottom navigation items
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

    // Sync selectedIndex with currentRoute
    val selectedIndex = navigationItems.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 1

    Scaffold(
        topBar = {
            PhotoTopAppBar(
                onAddClick = onAddClick,
                onNotificationClick = onNotificationClick,
                onProfileClick = onProfileClick
            )
        },
        bottomBar = {
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
            // Quick access items (Favourites, Bin)
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
            // Regular collection items with thumbnails
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Thumbnail image
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

                // Collection info
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
