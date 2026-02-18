package com.example.photoclone.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Google Photos-style Create Screen
 *
 * Features:
 * - Creative header with overlapping photos
 * - Tools grid (using chunked rows to avoid nested scrolling)
 * - Template sections with horizontal scrolling
 * - Dark theme throughout
 * - All in single LazyColumn for smooth scrolling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreenNew(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color(0xFF121212), // Dark background
        topBar = {
            CreateTopBar()
        },
        bottomBar = {
            GooglePhotos4TabBottomBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        CreateScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * Top App Bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Create",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

/**
 * Main content in single LazyColumn
 */
@Composable
private fun CreateScreenContent(modifier: Modifier = Modifier) {
    // Sample data
    val tools = remember {
        listOf(
            CreateTool("collage", "Collage", Icons.Outlined.ViewModule),
            CreateTool("highlight", "Highlight video", Icons.Outlined.Movie),
            CreateTool("animation", "Animation", Icons.Outlined.Animation),
            CreateTool("cinematic", "Cinematic photo", Icons.Outlined.CameraAlt),
            CreateTool("album", "Album", Icons.Outlined.PhotoAlbum),
            CreateTool("shared", "Shared album", Icons.Outlined.PersonAdd)
        )
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Creative Header with overlapping photos
        item {
            CreateHeader()
        }

        // Spacing
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section: Tools
        item {
            SectionTitle(
                title = "Tools",
                onViewAllClick = null
            )
        }

        // Tools Grid (using chunked to avoid nested scrolling)
        items(tools.chunked(2)) { rowTools ->
            ToolsGridRow(tools = rowTools)
        }
    }
}

/**
 * Creative header with overlapping photos and "Create" text
 */
@Composable
private fun CreateHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background photo 1 (rotated and offset)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://picsum.photos/300/400?random=10")
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp, 160.dp)
                .offset(x = (-40).dp, y = 20.dp)
                .rotate(-12f)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .zIndex(1f),
            contentScale = ContentScale.Crop
        )

        // Background photo 2 (rotated opposite direction)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://picsum.photos/300/400?random=11")
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp, 160.dp)
                .offset(x = 40.dp, y = 10.dp)
                .rotate(8f)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .zIndex(2f),
            contentScale = ContentScale.Crop
        )

        // "Create" text in center (on top)
        Surface(
            modifier = Modifier
                .zIndex(3f)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF212121)
        ) {
            Text(
                text = "Create",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )
        }
    }
}

/**
 * Section title with optional "View all" button
 */
@Composable
private fun SectionTitle(
    title: String,
    onViewAllClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        if (onViewAllClick != null) {
            TextButton(onClick = onViewAllClick) {
                Text(
                    "View all",
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Row of 2 tool cards (for grid layout without nested scrolling)
 */
@Composable
private fun ToolsGridRow(tools: List<CreateTool>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        tools.forEach { tool ->
            ToolCard(
                tool = tool,
                onClick = { /* TODO: Handle tool click */ },
                modifier = Modifier.weight(1f)
            )
        }

        // Fill empty space if only one tool in row
        if (tools.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * Individual tool card
 */
@Composable
private fun ToolCard(
    tool: CreateTool,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF212121)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.title,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tool.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

/**
 * Bottom navigation bar (4 tabs)
 */
@Composable
private fun GooglePhotos4TabBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF1E1E1E),
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "photos",
            onClick = { onNavigate("photos") },
            icon = {
                Icon(
                    if (currentRoute == "photos") Icons.Filled.Photo else Icons.Outlined.Photo,
                    "Photos"
                )
            },
            label = { Text("Photos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.White.copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "collections",
            onClick = { onNavigate("collections") },
            icon = {
                Icon(
                    if (currentRoute == "collections") Icons.Filled.Collections else Icons.Outlined.Collections,
                    "Collections"
                )
            },
            label = { Text("Collections") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.White.copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "create",
            onClick = { onNavigate("create") },
            icon = {
                Icon(
                    if (currentRoute == "create") Icons.Filled.AddCircle else Icons.Outlined.AddCircle,
                    "Create"
                )
            },
            label = { Text("Create") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.White.copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "search",
            onClick = { onNavigate("search") },
            icon = {
                Icon(
                    if (currentRoute == "search") Icons.Filled.Search else Icons.Outlined.Search,
                    "Search"
                )
            },
            label = { Text("Search") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

/**
 * Data model for create tools
 */
private data class CreateTool(
    val id: String,
    val title: String,
    val icon: ImageVector
)

