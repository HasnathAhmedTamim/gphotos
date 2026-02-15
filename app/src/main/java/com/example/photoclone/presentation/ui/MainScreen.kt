package com.example.photoclone.presentation.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    // Persistent sheet state
    var sheetExpanded by remember { mutableStateOf(false) }

    // sample "photos" as ids
    val photos = remember { List(60) { it } }
    val selected = remember { mutableStateListOf<Int>() }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Content: photo grid
        PhotoGrid(
            photos = photos,
            selected = selected,
            onToggleSelect = { id ->
                if (selected.contains(id)) selected.remove(id) else selected.add(id)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        )

        // Sheet sizes based on available height
        val maxH = this.maxHeight
        val peekHeight = 96.dp
        val expandedHeight = (maxH * 0.6f)
        val sheetHeight by animateDpAsState(targetValue = if (sheetExpanded) expandedHeight else peekHeight)

        // Small control row floating above content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Photos", fontWeight = FontWeight.Bold)
            Button(onClick = { sheetExpanded = !sheetExpanded }) {
                Text(if (sheetExpanded) "Close" else "Open")
            }
        }

        // Persistent bottom sheet
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetHeight)
                .align(Alignment.BottomCenter)
                .zIndex(1f),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Drag handle (tap to toggle)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .padding(top = 8.dp)
                        .background(Color.LightGray, RoundedCornerShape(2.dp))
                        .clickable { sheetExpanded = !sheetExpanded }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sheet content
                BottomSheetContent(onCreateClick = { /* handle create */ })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGrid(
    photos: List<Int>,
    selected: List<Int>,
    onToggleSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(photos) { id ->
            PhotoItem(
                id = id,
                isSelected = selected.contains(id),
                onClick = { onToggleSelect(id) }
            )
        }
    }
}

@Composable
fun PhotoItem(id: Int, isSelected: Boolean, onClick: () -> Unit) {
    val bg = if (isSelected) Color(0xFF90CAF9) else Color(0xFFEEEEEE)
    Box(
        modifier = Modifier
            .padding(6.dp)
            .aspectRatio(1f)
            .background(bg, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.PhotoAlbum, contentDescription = null, tint = Color.DarkGray)
            Text("ID $id", color = Color.DarkGray)
        }
    }
}

@Composable
fun BottomSheetContent(onCreateClick: (Int) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text("Create new", fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCreateClick(1) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.PhotoAlbum, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Album")
                Text("Create a new album", style = MaterialTheme.typography.bodySmall)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCreateClick(2) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.PhotoAlbum, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Import")
                Text("Import from device", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Tip: tap the handle to expand/collapse", style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
