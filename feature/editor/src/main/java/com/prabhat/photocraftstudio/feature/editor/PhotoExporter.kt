package com.prabhat.photocraftstudio.feature.editor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.designsystem.components.loadPhotoBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PhotoExporter {

    suspend fun export(
        context: Context,
        photo: PhotoItem,
        edit: EditState,
        outputUri: Uri,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val source = loadPhotoBitmap(context, photo.imageModel, maxDimension = 2560)
                ?: error("Unable to load the selected photo")

            val transform = Matrix().apply {
                postRotate(edit.rotationQuarterTurns * 90f)
                if (edit.flipHorizontal) {
                    postScale(-1f, 1f)
                }
            }
            val transformed = Bitmap.createBitmap(
                source,
                0,
                0,
                source.width,
                source.height,
                transform,
                true,
            )

            val output = Bitmap.createBitmap(
                transformed.width,
                transformed.height,
                Bitmap.Config.ARGB_8888,
            )
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                colorFilter = ColorMatrixColorFilter(edit.toAndroidColorMatrix())
            }
            Canvas(output).drawBitmap(transformed, 0f, 0f, paint)

            val stream = context.contentResolver.openOutputStream(outputUri)
                ?: error("Unable to open the export destination")
            stream.use { destination ->
                if (!output.compress(Bitmap.CompressFormat.JPEG, 94, destination)) {
                    error("Unable to encode the edited photo")
                }
            }

            if (transformed !== source) transformed.recycle()
            output.recycle()
            source.recycle()
        }
    }
}
