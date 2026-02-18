package com.example.photoclone.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Google Photos Style Search Screen
 * - Search bar at top
 * - Category suggestions
 * - People & Pets section
 * - Places section
 * - Things section
 * - Recent searches
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    currentRoute: String = "search",
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SearchTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearchActive = isSearchActive,
                onSearchActiveChange = { isSearchActive = it }
            )
        },
        bottomBar = {
            GooglePhotos4TabBottomBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (isSearchActive) {
            // Search results view
            SearchResultsView(
                searchQuery = searchQuery,
                modifier = modifier.padding(paddingValues)
            )
        } else {
            // Search home view with categories
            SearchHomeView(
                modifier = modifier.padding(paddingValues)
            )
        }
    }
}

/**
 * Search top bar with SearchBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = {
                onSearchActiveChange(true)
            },
            active = false,
            onActiveChange = { onSearchActiveChange(it) },
            placeholder = {
                Text(
                    "Search your photos",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Voice search icon
                    IconButton(onClick = { /* TODO: Voice search */ }) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Voice search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Search suggestions would go here
        }
    }
}

/**
 * Search home view with categories
 */
@Composable
private fun SearchHomeView(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        // Category chips
        item {
            SearchCategoryChips()
        }

        // Spacing
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // People & Pets section
        item {
            SearchSectionHeader(
                title = "People & pets",
                onViewAllClick = { /* TODO */ }
            )
        }

        item {
            PeopleCarousel()
        }

        // Spacing
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Places section
        item {
            SearchSectionHeader(
                title = "Places",
                onViewAllClick = { /* TODO */ }
            )
        }

        item {
            PlacesGrid()
        }

        // Spacing
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Things section
        item {
            SearchSectionHeader(
                title = "Things",
                onViewAllClick = { /* TODO */ }
            )
        }

        item {
            ThingsGrid()
        }
    }
}

/**
 * Search category chips
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchCategoryChips() {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(searchCategories) { category ->
            FilterChip(
                selected = false,
                onClick = { /* TODO */ },
                label = {
                    Text(
                        category.name,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

/**
 * Section header with "View all" button
 */
@Composable
private fun SearchSectionHeader(
    title: String,
    onViewAllClick: () -> Unit
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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        TextButton(onClick = onViewAllClick) {
            Text(
                "View all",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * People & Pets carousel
 */
@Composable
private fun PeopleCarousel() {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(samplePeople) { person ->
            PersonCard(person)
        }
    }
}

/**
 * Individual person card
 */
@Composable
private fun PersonCard(person: SearchPerson) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { /* TODO */ },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular photo
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(person.photoUrl)
                .crossfade(200)
                .build(),
            contentDescription = person.name,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Name
        Text(
            text = person.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2
        )

        // Photo count
        Text(
            text = "${person.photoCount} photos",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Places grid (2 columns)
 */
@Composable
private fun PlacesGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(samplePlaces.take(4)) { place ->
            PlaceCard(place)
        }
    }
}

/**
 * Individual place card
 */
@Composable
private fun PlaceCard(place: SearchPlace) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ }
    ) {
        // Place photo
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(place.photoUrl)
                .crossfade(200)
                .build(),
            contentDescription = place.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Place name
        Text(
            text = place.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )

        // Photo count
        Text(
            text = "${place.photoCount} photos",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Things grid (3 columns)
 */
@Composable
private fun ThingsGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(sampleThings.take(6)) { thing ->
            ThingCard(thing)
        }
    }
}

/**
 * Individual thing card
 */
@Composable
private fun ThingCard(thing: SearchThing) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Thing photo
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thing.photoUrl)
                .crossfade(200)
                .build(),
            contentDescription = thing.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Thing name
        Text(
            text = thing.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

/**
 * Search results view (when searching)
 */
@Composable
private fun SearchResultsView(
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (searchQuery.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Search your photos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Show search results here
            Text(
                "Search results for: \"$searchQuery\"",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Bottom navigation bar
 */
@Composable
private fun GooglePhotos4TabBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "photos",
            onClick = { onNavigate("photos") },
            icon = {
                Icon(
                    if (currentRoute == "photos") Icons.Filled.Photo else Icons.Outlined.Photo,
                    contentDescription = "Photos"
                )
            },
            label = {
                Text(
                    "Photos",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        NavigationBarItem(
            selected = currentRoute == "collections",
            onClick = { onNavigate("collections") },
            icon = {
                Icon(
                    if (currentRoute == "collections") Icons.Filled.Collections else Icons.Outlined.Collections,
                    contentDescription = "Collections"
                )
            },
            label = {
                Text(
                    "Collections",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        NavigationBarItem(
            selected = currentRoute == "create",
            onClick = { onNavigate("create") },
            icon = {
                Icon(
                    if (currentRoute == "create") Icons.Filled.AddCircle else Icons.Outlined.AddCircle,
                    contentDescription = "Create"
                )
            },
            label = {
                Text(
                    "Create",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        NavigationBarItem(
            selected = currentRoute == "search",
            onClick = { onNavigate("search") },
            icon = {
                Icon(
                    if (currentRoute == "search") Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Search"
                )
            },
            label = {
                Text(
                    "Search",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

// Data models
data class SearchCategory(val name: String, val icon: ImageVector)
data class SearchPerson(val name: String, val photoUrl: String, val photoCount: Int)
data class SearchPlace(val name: String, val photoUrl: String, val photoCount: Int)
data class SearchThing(val name: String, val photoUrl: String)

// Sample data
private val searchCategories = listOf(
    SearchCategory("Screenshots", Icons.Outlined.Screenshot),
    SearchCategory("Selfies", Icons.Outlined.Portrait),
    SearchCategory("Videos", Icons.Outlined.VideoLibrary),
    SearchCategory("Documents", Icons.Outlined.Description),
    SearchCategory("Favorites", Icons.Outlined.FavoriteBorder)
)

private val samplePeople = listOf(
    SearchPerson("John Doe", "https://picsum.photos/200/200?random=1", 45),
    SearchPerson("Jane Smith", "https://picsum.photos/200/200?random=2", 32),
    SearchPerson("Mike Johnson", "https://picsum.photos/200/200?random=3", 28),
    SearchPerson("Sarah Williams", "https://picsum.photos/200/200?random=4", 56),
    SearchPerson("Tom Brown", "https://picsum.photos/200/200?random=5", 19)
)

private val samplePlaces = listOf(
    SearchPlace("New York", "https://picsum.photos/400/300?random=10", 124),
    SearchPlace("Paris", "https://picsum.photos/400/300?random=11", 89),
    SearchPlace("Tokyo", "https://picsum.photos/400/300?random=12", 67),
    SearchPlace("London", "https://picsum.photos/400/300?random=13", 45)
)

private val sampleThings = listOf(
    SearchThing("Food", "https://picsum.photos/300/300?random=20"),
    SearchThing("Pets", "https://picsum.photos/300/300?random=21"),
    SearchThing("Cars", "https://picsum.photos/300/300?random=22"),
    SearchThing("Nature", "https://picsum.photos/300/300?random=23"),
    SearchThing("Art", "https://picsum.photos/300/300?random=24"),
    SearchThing("Sports", "https://picsum.photos/300/300?random=25")
)

