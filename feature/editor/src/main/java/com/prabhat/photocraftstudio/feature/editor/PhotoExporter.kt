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
import kotlin.math.roundToInt

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
            applyRgbCurves(output, edit.curves)

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

    fun applyRgbCurves(target: Bitmap, curves: RgbCurves) {
        val width = target.width
        val height = target.height
        val pixels = IntArray(width * height)
        target.getPixels(pixels, 0, width * 4, 0, 0, width, height)
        for (index in pixels.indices) {
            val argb = pixels[index]
            val r = ((argb shr 16) and 0xFF) / 255f
            val g = ((argb shr 8) and 0xFF) / 255f
            val b = (argb and 0xFF) / 255f
            val redValue = curves.red.valueAt(r)
            val greenValue = curves.green.valueAt(g)
            val blueValue = curves.blue.valueAt(b)
            val finalRed = curves.master.valueAt(redValue)
            val finalGreen = curves.master.valueAt(greenValue)
            val finalBlue = curves.master.valueAt(blueValue)
            val alpha = (argb shr 24) and 0xFF
            val redInt = (finalRed * 255f).roundToInt()
            val greenInt = (finalGreen * 255f).roundToInt()
            val blueInt = (finalBlue * 255f).roundToInt()
            pixels[index] = (alpha shl 24) or (redInt shl 16) or (greenInt shl 8) or blueInt
        }
        target.setPixels(pixels, 0, width, 0, 0, width, height)
    }
}
