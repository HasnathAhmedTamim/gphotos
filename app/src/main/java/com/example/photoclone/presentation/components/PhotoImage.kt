package com.example.photoclone.presentation.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.Disposable
import coil.request.ImageRequest
import coil.size.Size
import com.example.photoclone.R

/**
 * Small reusable composable that encapsulates Coil image loading configuration used across the app.
 * - imageUrl: remote or local url string
 * - contentDescription: accessibility text (caller should localize via stringResource)
 * - requestSizePx: optional size hint to help Coil decode the image at an appropriate size
 * - showPlaceholder: whether to show a placeholder drawable while loading
 */
@Composable
fun PhotoImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    requestSizePx: Int? = null,
    showPlaceholder: Boolean = true
) {
    val context = LocalContext.current
    val request = remember(imageUrl, requestSizePx) {
        val builder = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
        requestSizePx?.let { builder.size(Size(it, it)) }
        // Using software bitmaps is more compatible with content:// URIs across Android versions/devices.
        builder.allowHardware(false)
        builder.build()
    }

    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        placeholder = if (showPlaceholder) painterResource(R.drawable.ic_photo_placeholder) else null,
        error = painterResource(R.drawable.ic_broken_image)
    )
}

/**
 * Prefetch an image into Coil's memory/disk cache. Call from a coroutine or a LaunchedEffect.
 */
fun prefetchImage(context: Context, url: String?) : Disposable? {
    if (url.isNullOrBlank()) return null
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .size(Size.ORIGINAL)
        .allowHardware(false)
        .build()
    return loader.enqueue(request)
}
