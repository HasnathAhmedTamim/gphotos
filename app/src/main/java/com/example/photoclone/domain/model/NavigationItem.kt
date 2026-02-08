package com.example.photoclone.domain.model

sealed class NavigationItem(
    val route: String,
    val title: String,
    val iconResId: String
) {
    object Photos : NavigationItem("photos", "Photos", "ic_photo_library")
    object Collection : NavigationItem("collection", "Collection", "ic_collections")
    object Create : NavigationItem("create", "Create", "ic_add_circle")
    object Search : NavigationItem("search", "Search", "ic_search")
}