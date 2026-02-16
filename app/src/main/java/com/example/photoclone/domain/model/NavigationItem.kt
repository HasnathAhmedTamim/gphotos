@file:Suppress("unused")

package com.example.photoclone.domain.model

// NavigationItem represents a screen in the bottom/top navigation.
// route: navigation route, title: display text, iconResId: drawable resource name (string here).
sealed class NavigationItem(
    val route: String,
    val title: String,
    val iconResId: String
) {
    // Photos grid/list screen
    object Photos : NavigationItem("photos", "Photos", "ic_photo_library")

    // User's collection or saved items
    object Collection : NavigationItem("collection", "Collection", "ic_collections")

    // Action to create/add new content
    object Create : NavigationItem("create", "Create", "ic_add_circle")

    // Search screen
    object Search : NavigationItem("search", "Search", "ic_search")
}