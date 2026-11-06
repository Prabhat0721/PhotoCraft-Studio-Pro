package com.prabhat.photocraftstudio.core.designsystem.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

@Composable
fun PhotoCraftAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    colorFilter: ColorFilter? = null,
    maxDimension: Int = 2048,
    containerColor: Color = MaterialTheme.photoCraftColors.card,
) {
    val context = LocalContext.current
    val image by produceState<ImageBitmap?>(initialValue = null, model, maxDimension) {
        value = withContext(Dispatchers.IO) {
            loadPhotoBitmap(context, model, maxDimension)?.asImageBitmap()
        }
    }

    Box(modifier = modifier.background(containerColor)) {
        image?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
                colorFilter = colorFilter,
            )
        }
    }
}

fun loadPhotoBitmap(context: Context, model: Any?, maxDimension: Int = 2048): Bitmap? {
    if (model == null) return null
    return runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        openPhotoStream(context, model)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, bounds)
        } ?: return null

        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sampleSize = 1
        while (max(bounds.outWidth, bounds.outHeight) / sampleSize > maxDimension) {
            sampleSize *= 2
        }

        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        openPhotoStream(context, model)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
    }.getOrNull()
}

private fun openPhotoStream(context: Context, model: Any) = when (model) {
    is Int -> context.resources.openRawResource(model)
    is Uri -> context.contentResolver.openInputStream(model)
    else -> null
}
