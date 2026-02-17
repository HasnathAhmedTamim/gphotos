# 🧪 Quick Test Card - Photo Viewer

## ✅ What Was Fixed

1. **Image blur** → Now loads ORIGINAL size
2. **Swipe architecture** → Verified correct (Google Photos pattern)

---

## 📋 5-Minute Test Protocol

### **Test 1: Image Sharpness** (30 sec)
```
1. Open app
2. Tap any photo
3. Wait 2 seconds for load
4. Look at image quality

✅ PASS: Image is sharp
❌ FAIL: Image is blurry → See "Blurry Fix" below
```

---

### **Test 2: Swipe Navigation** (30 sec)
```
1. Open viewer
2. Don't zoom/touch anything
3. Swipe left once
4. Swipe right once

✅ PASS: Photos change smoothly
❌ FAIL: Nothing happens → See "Swipe Debug" below
```

---

### **Test 3: Zoom Blocks Swipe** (30 sec)
```
1. Open viewer
2. Double-tap to zoom
3. Try swiping left/right

✅ PASS: Swipe doesn't work (correct!)
❌ FAIL: Swipe works → Zoom not blocking (issue)
```

---

### **Test 4: Unzoom Re-enables Swipe** (30 sec)
```
1. While zoomed (from Test 3)
2. Double-tap to unzoom
3. Try swiping left/right

✅ PASS: Swipe works again
❌ FAIL: Swipe still blocked → Scale stuck > 1
```

---

### **Test 5: All Gestures** (1 min)
```
Single tap → UI toggles       ✅ / ❌
Double tap → Zoom toggles     ✅ / ❌
Pinch → Smooth zoom           ✅ / ❌
Drag (zoomed) → Pan works     ✅ / ❌
Swipe (normal) → Navigate     ✅ / ❌
```

---

## 🔧 Quick Fixes

### **If Images Are Still Blurry:**

**Cause:** Mock images are 400x400 (too small)

**Fix (2 minutes):**
1. Open `Navigation.kt`
2. Find line ~43:
   ```kotlin
   val demoPhotos = List(30) { index ->
       "https://picsum.photos/400/400?seed=$index&blur=${index % 5}"
   }
   ```
3. Change to:
   ```kotlin
   val demoPhotos = List(30) { index ->
       "https://picsum.photos/2000/2000?seed=$index"
   }
   ```
4. Rebuild & test

---

### **If Swipe Doesn't Work:**

**Debug Steps:**

1. **Add debug text to PhotoPager:**
   ```kotlin
   Text(
       "Scale: $currentPageScale | Page: ${pagerState.currentPage}",
       modifier = Modifier.align(Alignment.TopStart).background(Color.Red).padding(8.dp)
   )
   ```

2. **Rebuild & open viewer:**
   - Check what Scale shows
   - Try swiping and watch Page number

3. **Interpret results:**
   - Scale > 1.0 → Zoom didn't reset, expected behavior
   - Scale = 1.0, Page doesn't change → Gesture conflict
   - Scale = 1.0, Page changes → Working correctly!

---

## 📊 Expected Results

### **✅ All Passing:**
```
✅ Images are sharp
✅ Swipe works when not zoomed
✅ Swipe blocked when zoomed (correct)
✅ Swipe re-enabled after unzoom
✅ All 5 gestures working
```

**Result:** Google Photos UX achieved! 🎉

---

### **⚠️ Some Failing:**

| Failure | Likely Cause | Fix |
|---------|--------------|-----|
| Blurry | Mock images 400x400 | Use 2000x2000 URLs |
| No swipe | Scale stuck > 1 | Reset zoom before testing |
| No swipe | Gesture conflict | Check parent composables |
| No zoom | Gesture not registered | Check pointer input |

---

## 🎯 Success Criteria

**Minimum for "Working":**
- ✅ Test 1 passes (sharp images)
- ✅ Test 2 passes (swipe works)
- ✅ Test 3 passes (zoom blocks swipe)

**Full Google Photos Experience:**
- ✅ All 5 tests pass
- ✅ Smooth animations
- ✅ No lag/stutter

---

## 📱 Test Device Info

**Record your results:**
```
Device: _________________
Android: ________________
Test 1 (Sharpness): ✅ / ❌
Test 2 (Swipe): ✅ / ❌
Test 3 (Zoom blocks): ✅ / ❌
Test 4 (Unzoom re-enables): ✅ / ❌
Test 5 (All gestures): ✅ / ❌

Notes:
_________________________
_________________________
```

---

## 🚀 Quick Commands

**Build:**
```bash
cd E:\PhotoClone
.\gradlew assembleDebug
```

**Install:**
```bash
.\gradlew installDebug
```

**Logs:**
```bash
adb logcat | findstr "PhotoPager"
```

---

**Time to complete:** ~5 minutes  
**Expected result:** All tests passing  
**If not:** Follow "Quick Fixes" above
