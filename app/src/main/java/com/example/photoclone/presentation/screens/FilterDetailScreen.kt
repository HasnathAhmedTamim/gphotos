package com.example.photoclone.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDetailScreen(filterType: String, onBack: () -> Unit, onApply: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(filterType.replaceFirstChar { it.uppercase() }) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    TextButton(onClick = { onApply(filterType) }) {
                        Text("Apply")
                    }
                }
            )
        }
    ) { padding ->
        val features = when (filterType.lowercase()) {
            "recent" -> listOf("Sort by new", "Group by day", "Create memory")
            "favorites" -> listOf("Share favorites", "Create album", "Unfavorite")
            "videos" -> listOf("Trim video", "Create highlight", "Export")
            else -> listOf("No features")
        }

        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(features) { feature ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)) {
                    ListItem(
                        headlineContent = { Text(feature) },
                        leadingContent = {
                            when (filterType.lowercase()) {
                                "recent" -> Icon(Icons.Filled.Schedule, contentDescription = null)
                                "favorites" -> Icon(Icons.Filled.Favorite, contentDescription = null)
                                "videos" -> Icon(Icons.Filled.PlayCircle, contentDescription = null)
                                else -> Icon(Icons.Filled.Schedule, contentDescription = null)
                            }
                        }
                    )
                }
            }
        }
    }
}
