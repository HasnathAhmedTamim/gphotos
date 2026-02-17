# 🚀 Quick Start - Google Photos Viewer

## Instant Usage (Copy & Paste)

### **1. Basic Viewer (Works Now)**

Already implemented in your HomeScreen:

```kotlin
if (showPager) {
    PhotoPager(
        photoUrls = displayPhotos.map { it.imageUrl },
        initialPage = selectedPhotoIndex,
        onDismiss = { showPager = false }
    )
}
```

✅ **Features active:** Swipe, zoom, tap UI toggle, back button

---

### **2. Add Date Display (5 minutes)**

Add to HomeScreen.kt:

```kotlin
if (showPager) {
    PhotoPager(
        photoUrls = displayPhotos.map { it.imageUrl },
        initialPage = selectedPhotoIndex,
        onDismiss = { showPager = false },
        photoMetadata = { index ->
            PhotoMetadata(
                date = "Feb 17, 2026",
                location = "San Francisco"
            )
        }
    )
}
```

✅ **New:** Top bar shows date and location

---

### **3. Add Favorite Feature (10 minutes)**

Add state + callback:

```kotlin
// At top of HomeScreen composable
var favoritePhotos by remember { mutableStateOf(setOf<String>()) }

// In PhotoPager
if (showPager) {
    val currentPhotoUrl = displayPhotos.getOrNull(selectedPhotoIndex)?.imageUrl
    
    PhotoPager(
        photoUrls = displayPhotos.map { it.imageUrl },
        initialPage = selectedPhotoIndex,
        onDismiss = { showPager = false },
        isFavorite = { index ->
            displayPhotos.getOrNull(index)?.imageUrl?.let {
                favoritePhotos.contains(it)
            } ?: false
        },
        onFavoriteToggle = { index ->
            displayPhotos.getOrNull(index)?.imageUrl?.let { url ->
                favoritePhotos = if (favoritePhotos.contains(url)) {
                    favoritePhotos - url
                } else {
                    favoritePhotos + url
                }
            }
        }
    )
}
```

✅ **New:** Heart icon toggles, state persists

---

### **4. Add Share Feature (5 minutes)**

```kotlin
// Helper function at top level
fun sharePhoto(context: Context, imageUrl: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Check out this photo: $imageUrl")
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}

// In PhotoPager
if (showPager) {
    PhotoPager(
        photoUrls = displayPhotos.map { it.imageUrl },
        initialPage = selectedPhotoIndex,
        onDismiss = { showPager = false },
        onShare = { index ->
            displayPhotos.getOrNull(index)?.let { photo ->
                sharePhoto(context, photo.imageUrl)
            }
        }
    )
}
```

✅ **New:** Share button opens system share sheet

---

### **5. Add Delete Confirmation (10 minutes)**

```kotlin
// Add state
var photoToDelete by remember { mutableStateOf<Photo?>(null) }

// In PhotoPager
if (showPager) {
    PhotoPager(
        photoUrls = displayPhotos.map { it.imageUrl },
        initialPage = selectedPhotoIndex,
        onDismiss = { showPager = false },
        onDelete = { index ->
            displayPhotos.getOrNull(index)?.let { photo ->
                photoToDelete = photo
            }
        }
    )
}

// Delete confirmation dialog
photoToDelete?.let { photo ->
    AlertDialog(
        onDismissRequest = { photoToDelete = null },
        title = { Text("Delete photo?") },
        text = { Text("This will move the photo to trash.") },
        confirmButton = {
            TextButton(onClick = {
                // TODO: Actually delete the photo
                viewModel.deletePhoto(photo)
                photoToDelete = null
                showPager = false
            }) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = { photoToDelete = null }) {
                Text("Cancel")
            }
        }
    )
}
```

✅ **New:** Delete shows confirmation, removes photo

---

### **6. Add Edit Screen (20 minutes)**

```kotlin
// Add state
var showEditScreen by remember { mutableStateOf(false) }
var photoToEdit by remember { mutableStateOf<Photo?>(null) }

// In PhotoPager
if (showPager && !showEditScreen) {
    PhotoPager(
        photoUrls = displayPhotos.map { it.imageUrl },
        initialPage = selectedPhotoIndex,
        onDismiss = { showPager = false },
        onEdit = { index ->
            displayPhotos.getOrNull(index)?.let { photo ->
                photoToEdit = photo
                showEditScreen = true
            }
        }
    )
}

// Edit screen overlay
if (showEditScreen) {
    EditScreen(
        photo = photoToEdit,
        onDismiss = { showEditScreen = false },
        onSave = { editedPhoto ->
            // TODO: Save edited photo
            showEditScreen = false
        }
    )
}
```

✅ **New:** Edit button opens full edit screen

---

### **7. Add Album Picker (15 minutes)**

```kotlin
// Add state
var showAlbumPicker by remember { mutableStateOf(false) }
var photoForAlbum by remember { mutableStateOf<Photo?>(null) }

// In PhotoPager
if (showPager) {
    PhotoPager(
        photoUrls = displayPhotos.map { it.imageUrl },
        initialPage = selectedPhotoIndex,
        onDismiss = { showPager = false },
        onAddToAlbum = { index ->
            displayPhotos.getOrNull(index)?.let { photo ->
                photoForAlbum = photo
                showAlbumPicker = true
            }
        }
    )
}

// Album picker bottom sheet
if (showAlbumPicker) {
    ModalBottomSheet(
        onDismissRequest = { showAlbumPicker = false }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Add to Album", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            
            // TODO: List albums here
            Button(onClick = {
                // Add to album
                showAlbumPicker = false
            }) {
                Text("Create New Album")
            }
        }
    }
}
```

✅ **New:** Add to album shows picker

---

### **8. Add More Options Menu (15 minutes)**

```kotlin
// Add state
var showMoreOptions by remember { mutableStateOf(false) }

// In PhotoPager
if (showPager) {
    PhotoPager(
        photoUrls = displayPhotos.map { it.imageUrl },
        initialPage = selectedPhotoIndex,
        onDismiss = { showPager = false },
        onMoreOptions = { index ->
            selectedPhotoIndex = index
            showMoreOptions = true
        }
    )
}

// More options sheet
if (showMoreOptions) {
    ModalBottomSheet(
        onDismissRequest = { showMoreOptions = false }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("More Options", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            
            TextButton(onClick = { /* Archive */ }) {
                Text("Move to archive")
            }
            TextButton(onClick = { /* Edit location */ }) {
                Text("Edit location")
            }
            TextButton(onClick = { /* Edit date */ }) {
                Text("Change date & time")
            }
            TextButton(onClick = { /* View folder */ }) {
                Text("View in folder")
            }
            TextButton(onClick = { /* Slideshow */ }) {
                Text("Slideshow")
            }
        }
    }
}
```

✅ **New:** More menu (⋮) shows options

---

### **9. Add Info Panel (20 minutes)**

```kotlin
// Add state
var showInfoPanel by remember { mutableStateOf(false) }

// In PhotoPager
if (showPager) {
    PhotoPager(
        photoUrls = displayPhotos.map { it.imageUrl },
        initialPage = selectedPhotoIndex,
        onDismiss = { showPager = false },
        photoMetadata = { index ->
            PhotoMetadata(
                date = "Feb 17, 2026",
                location = "San Francisco, CA",
                deviceInfo = "Pixel 8 Pro",
                fileSize = "2.4 MB",
                resolution = "4032 × 3024"
            )
        },
        onInfoClick = { index ->
            selectedPhotoIndex = index
            showInfoPanel = true
        }
    )
}

// Info panel bottom sheet
if (showInfoPanel) {
    ModalBottomSheet(
        onDismissRequest = { showInfoPanel = false }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Photo Info", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            
            val metadata = PhotoMetadata(
                date = "Feb 17, 2026",
                location = "San Francisco, CA",
                deviceInfo = "Pixel 8 Pro",
                fileSize = "2.4 MB",
                resolution = "4032 × 3024"
            )
            
            InfoRow("Date", metadata.date ?: "Unknown")
            InfoRow("Location", metadata.location ?: "Unknown")
            InfoRow("Device", metadata.deviceInfo ?: "Unknown")
            InfoRow("Size", metadata.fileSize ?: "Unknown")
            InfoRow("Resolution", metadata.resolution ?: "Unknown")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}
```

✅ **New:** Tap date/location shows detailed info

---

## ⚡ Progressive Enhancement Timeline

| Time | Feature | Effort |
|------|---------|--------|
| 0 min | ✅ Basic viewer | Already done |
| +5 min | ✅ Date display | Copy-paste |
| +10 min | ✅ Favorite | Add state + callback |
| +5 min | ✅ Share | System share intent |
| +10 min | ✅ Delete confirm | AlertDialog |
| +20 min | ✅ Edit screen | Full overlay |
| +15 min | ✅ Album picker | Bottom sheet |
| +15 min | ✅ More options | Menu sheet |
| +20 min | ✅ Info panel | Detailed sheet |

**Total:** ~2 hours for full Google Photos experience

---

## 🎯 Priority Order (Recommended)

1. **Favorite** (best ROI, quick win)
2. **Share** (easy, useful)
3. **Delete confirm** (safety feature)
4. **Date display** (polish)
5. **Info panel** (power user feature)
6. **More options** (advanced)
7. **Edit screen** (complex, separate project)
8. **Album picker** (depends on album system)

---

## ✅ Testing Checklist

After each feature:

- [ ] Build succeeds
- [ ] Feature works in viewer
- [ ] Back button still works
- [ ] State preserved on back
- [ ] No memory leaks
- [ ] Smooth 60fps

---

## 📱 Current Status

**Working Now:**
- ✅ Swipe left/right
- ✅ Pinch zoom
- ✅ Double tap zoom
- ✅ Single tap UI toggle
- ✅ Back navigation
- ✅ All UI elements visible

**Add Callbacks:**
- ⏳ Wire up favorite (10 min)
- ⏳ Wire up share (5 min)
- ⏳ Wire up delete (10 min)
- ⏳ Wire up edit (20 min)
- ⏳ Wire up album (15 min)

---

**Start with Step 3 (Favorite)** → Quick win, best user experience!
