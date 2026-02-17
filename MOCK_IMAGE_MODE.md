# Mock Image Mode Configuration

## Current Status: ✅ MOCK IMAGE MODE ACTIVE

The app is currently configured to use **mock/demo images** instead of loading real photos from the device gallery.

---

## What Was Changed

### HomeScreen.kt Modifications

1. **Commented Out Real Gallery Functionality**
   - Permission requests (READ_MEDIA_IMAGES / READ_EXTERNAL_STORAGE)
   - Permission launcher and image picker launcher
   - LaunchedEffect for permission checking
   - ViewModel gallery loading calls
   - Paging logic for large galleries

2. **Forced Mock Image Mode**
   - `permissionState` set to `false` (disables real gallery)
   - `displayPhotos` always built from `photos` parameter (mock images)
   - `usingPaging` forced to `false`
   - Simplified PhotoPager to only use in-memory lists
   - Removed AdaptivePagingGrid usage (commented out)

3. **Updated Debug Info**
   - Shows "MODE: MOCK IMAGES" instead of permission state
   - Shows photo count and selected count

---

## How to Switch Back to Real Gallery

### Step 1: Uncomment Permission Logic (Lines ~61-118)

Find this section:
```kotlin
// ========================================================================
// REAL GALLERY FUNCTIONALITY - COMMENTED OUT FOR MOCK IMAGE USAGE
// TODO: Uncomment the section below when ready to use real device gallery
// ========================================================================

/*
// Permission logic: choose the correct runtime permission for the SDK
...
*/
```

**Action:** Remove the `/*` and `*/` comment markers to uncomment the entire block.

### Step 2: Remove Mock Image Override (Lines ~119-130)

Find and **DELETE** this section:
```kotlin
// ========================================================================
// MOCK IMAGE MODE - CURRENTLY ACTIVE
// Using mock/demo images provided via the photos parameter
// ========================================================================

// Force permission state to false to disable real gallery loading
val permissionState: Boolean? = false

// Always use mock images from the photos parameter
val displayPhotos = photos.mapIndexed { index, url ->
    Photo(id = index.toLong(), imageUrl = url, thumbnailUrl = url)
}
```

### Step 3: Restore Original ViewModel Registration Logic

Find this:
```kotlin
// Register mock photos with ViewModel for selection state tracking
LaunchedEffect(displayPhotos) {
    if (displayPhotos.isNotEmpty()) {
        viewModel.setPhotos(displayPhotos)
    }
}
```

**Replace with:**
```kotlin
// If we're using the fallback demo photos, register them into the ViewModel so selection state works
LaunchedEffect(photoObjects.isEmpty()) {
    if (photoObjects.isEmpty()) {
        // Provide the in-memory displayPhotos to the ViewModel so its selection flows can update UI
        viewModel.setPhotos(displayPhotos)
    }
}
```

### Step 4: Uncomment Paging Logic (Lines ~245-320)

Find the commented paging section:
```kotlin
// ========================================================================
// PAGING LOGIC - COMMENTED OUT (for real gallery)
// TODO: Uncomment when using real device gallery
// ========================================================================
/*
// When permission granted, get paging items...
*/

// Force using in-memory mock images (no paging)
val usingPaging = false
val pagingItems = null
```

**Actions:**
1. Remove the comment markers around the paging logic
2. Delete the "Force using in-memory mock images" lines

### Step 5: Uncomment AdaptivePagingGrid (Lines ~280-350)

Find the large commented block:
```kotlin
// ========================================================================
// PAGING GRID - COMMENTED OUT (for real gallery)
// ========================================================================
/*
if (usingPaging && pagingItems != null) {
    AdaptivePagingGrid(
    ...
}
*/
```

**Action:** Remove the `/*` and `*/` markers to uncomment the entire AdaptivePagingGrid block.

### Step 6: Close the `else` Block Properly

After uncommenting AdaptivePagingGrid, ensure the `else` before `AdaptivePhotoGrid` is properly closed:
```kotlin
} else {
    AdaptivePhotoGrid(
    ...
}
```

### Step 7: Update Debug Info (Optional)

Change:
```kotlin
Text(text = "MODE: MOCK IMAGES", ...)
```

Back to:
```kotlin
Text(text = "perm:${permissionState}", ...)
```

And restore the "Reload" button if needed.

---

## Quick Reference: Key Sections

| Section | Line Range | Status |
|---------|------------|--------|
| Permission Logic | ~61-118 | ✅ Commented out |
| Mock Mode Override | ~119-130 | ✅ Active |
| ViewModel Registration | ~131-137 | ✅ Modified for mock |
| Paging Logic | ~245-250 | ✅ Commented out |
| PhotoPager Logic | ~252-265 | ✅ Simplified (mock only) |
| AdaptivePagingGrid | ~280-350 | ✅ Commented out |
| AdaptivePhotoGrid | ~355-410 | ✅ Active (mock images) |
| Debug Info | ~416-430 | ✅ Shows "MOCK IMAGE MODE" |

---

## Testing Mock Mode

1. Build and run the app
2. You should see the mock images from `photos` parameter in MainActivity
3. Selection, PhotoPager, and all features work with mock images
4. No permission requests will appear
5. Debug banner (if visible) shows "MODE: MOCK IMAGES"

---

## Benefits of Mock Mode

✅ **No permission hassles** - Test without granting storage access  
✅ **Faster development** - No need to connect physical device  
✅ **Predictable data** - Same images every time  
✅ **Works in emulator** - No need for real photos  
✅ **Easy debugging** - Controlled test data  

---

## Notes

- All real gallery functionality is preserved in comments - nothing was deleted
- Simply uncomment the marked sections to restore full gallery access
- The app architecture supports both modes without conflicts
- Mock images are provided via the `photos` parameter in MainActivity

---

**Current Mode:** Mock Images  
**Last Updated:** February 17, 2026  
**File Modified:** `HomeScreen.kt`
