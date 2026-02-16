package com.example.photoclone.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
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
        builder.allowHardware(true)
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
