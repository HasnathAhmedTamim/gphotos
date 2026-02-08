package com.example.photoclone.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photoclone.R
import com.example.photoclone.presentation.components.BottomNavItem
import com.example.photoclone.presentation.components.PhotoBottomNavigation
import com.example.photoclone.presentation.components.PhotoTopAppBar

data class CreateTool(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    currentRoute: String = "create",
    onAddClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onCreateClick: () -> Unit = {}
) {
    // Tools data
    val tools = listOf(
        CreateTool(
            icon = Icons.Outlined.AutoFixHigh,
            title = "Remix"
        ),
        CreateTool(
            icon = Icons.Outlined.Widgets,
            title = "Collage"
        ),
        CreateTool(
            icon = Icons.Outlined.VideoLibrary,
            title = "Highlight video"
        ),
        CreateTool(
            icon = Icons.Outlined.Camera,
            title = "Cinematic photo"
        ),
        CreateTool(
            icon = Icons.Outlined.Animation,
            title = "Animation"
        )
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
    val selectedIndex = navigationItems.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 2

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Center content with Create button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onCreateClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(28.dp),
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Text(
                        text = "Create",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Your tools section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "Your tools",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Tool cards in grid layout
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Row 1: Remix and Collage
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ToolCard(
                            tool = tools[0],
                            modifier = Modifier.weight(1f)
                        )
                        ToolCard(
                            tool = tools[1],
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Highlight video and Cinematic photo
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ToolCard(
                            tool = tools[2],
                            modifier = Modifier.weight(1f)
                        )
                        ToolCard(
                            tool = tools[3],
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 3: Animation (single card)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ToolCard(
                            tool = tools[4],
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ToolCard(
    tool: CreateTool,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { tool.onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = tool.title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
