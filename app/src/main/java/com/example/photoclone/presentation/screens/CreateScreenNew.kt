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
import com.example.photoclone.presentation.components.CreateNewBottomSheet
import com.example.photoclone.presentation.model.CreateAction
import com.example.photoclone.presentation.model.CreateSection

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
    // State for modal bottom sheet
    var showCreateSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
        Box(modifier = Modifier.fillMaxSize()) {
            CreateScreenContent(
                onCreateClick = { showCreateSheet = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )

            // Modal bottom sheet for create actions
            if (showCreateSheet) {
                CreateNewBottomSheet(
                    onDismiss = { showCreateSheet = false },
                    sections = getCreateSections(onNavigate)
                )
            }
        }
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
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Main content in single LazyColumn
 */
@Composable
private fun CreateScreenContent(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            CreateHeader(onCreateClick = onCreateClick)
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
 * Creative header with overlapping photos and Create button
 */
@Composable
private fun CreateHeader(onCreateClick: () -> Unit) {
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

        // Create button in center (on top) - same design as the main button
        Button(
            onClick = onCreateClick,
            modifier = Modifier
                .zIndex(3f)
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Create new",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
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
            color = MaterialTheme.colorScheme.onBackground
        )

        if (onViewAllClick != null) {
            TextButton(onClick = onViewAllClick) {
                Text(
                    "View all",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
        color = MaterialTheme.colorScheme.surfaceVariant
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tool.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
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
            label = { Text("Photos") }
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
            label = { Text("Collections") }
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
            label = { Text("Create") }
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
            label = { Text("Search") }
        )
    }
}

/**
 * Get the sections and actions for the Create New bottom sheet
 * Exactly matches Google Photos layout
 */
@Composable
fun getCreateSections(onNavigate: (String) -> Unit): List<CreateSection> {
    return listOf(
        // Primary actions (no title)
        CreateSection(
            title = null,
            actions = listOf(
                CreateAction(
                    id = "album",
                    title = "Album",
                    icon = Icons.Outlined.PhotoAlbum,
                    onClick = { onNavigate("create/album") }
                ),
                CreateAction(
                    id = "collage",
                    title = "Collage",
                    icon = Icons.Outlined.ViewModule,
                    onClick = { onNavigate("create/collage") }
                ),
                CreateAction(
                    id = "highlight_video",
                    title = "Highlight video",
                    icon = Icons.Outlined.Movie,
                    hasNewBadge = true,
                    onClick = { onNavigate("create/highlight_video") }
                ),
                CreateAction(
                    id = "cinematic_photo",
                    title = "Cinematic photo",
                    icon = Icons.Outlined.CameraAlt,
                    onClick = { onNavigate("create/cinematic_photo") }
                ),
                CreateAction(
                    id = "animation",
                    title = "Animation",
                    icon = Icons.Outlined.Animation,
                    onClick = { onNavigate("create/animation") }
                ),
                CreateAction(
                    id = "remix",
                    title = "Remix",
                    icon = Icons.Outlined.Shuffle,
                    onClick = { onNavigate("create/remix") }
                )
            )
        ),
        // Secondary actions
        CreateSection(
            title = null,
            actions = listOf(
                CreateAction(
                    id = "get_photos",
                    title = "Get photos",
                    icon = Icons.Outlined.Download,
                    onClick = { onNavigate("create/get_photos") }
                ),
                CreateAction(
                    id = "share_partner",
                    title = "Share with a partner",
                    icon = Icons.Outlined.PersonAdd,
                    onClick = { onNavigate("create/share_partner") }
                ),
                CreateAction(
                    id = "import",
                    title = "Import from other places",
                    icon = Icons.Outlined.CloudUpload,
                    onClick = { onNavigate("create/import") }
                )
            )
        )
    )
}

/**
 * Data model for create tools
 */
private data class CreateTool(
    val id: String,
    val title: String,
    val icon: ImageVector
)
